import { Component, NgZone, OnInit, inject } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { concatMap, finalize, map } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ItemCountComponent } from 'app/shared/pagination';
import { FormsModule } from '@angular/forms';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { FilterComponent, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { IMilitar } from '../militar.model';
import { EntityArrayResponseType, MilitarService } from '../service/militar.service';
import { MilitarDeleteDialogComponent } from '../delete/militar-delete-dialog.component';
import { MilitarFormService } from '../update/militar-form.service';
import { IPosto } from 'app/entities/posto/posto.model';
import { PostoService } from 'app/entities/posto/service/posto.service';

@Component({
  standalone: true,
  selector: 'jhi-militar',
  templateUrl: './militar.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    FilterComponent,
    ItemCountComponent,
  ],
})
export class MilitarComponent implements OnInit {
  private static readonly NOT_SORTABLE_FIELDS_AFTER_SEARCH = [
    'saram',
    'nomeCompleto',
    'nomeGuerra',
    'om',
    'telefone',
    'statusMilitar',
    'forca',
    'nrAntiguidade',
    'ultimaPromocao',
    'cpf',
    'email'
  ];

  militar: IMilitar | null = null;

  subscription: Subscription | null = null;
  militars?: IMilitar[];
  isLoading = false;

  isSaving = false;

  sortState = sortStateSignal({});
  currentSearch = '';
  filters: IFilterOptions = new FilterOptions();

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  postosSharedCollection: IPosto[] = [];
  filteredPostos: [] | undefined;

  public router = inject(Router);
  protected militarService = inject(MilitarService);
  protected activatedRoute = inject(ActivatedRoute);
  protected sortService = inject(SortService);
  protected militarFormService = inject(MilitarFormService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);
  protected postoService = inject(PostoService);
  
  trackId = (_index: number, item: IMilitar): number => this.militarService.getMilitarIdentifier(item);
  

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => { this.fillComponentAttributeFromRoute(params, data); }),
        tap(() => { this.load(); }),
      )
      .subscribe();

    this.filters.filterChanges.subscribe(filterOptions => { this.handleNavigation(1, this.sortState(), filterOptions); });

    this.loadRelationshipsOptions();
  }

  previousState(): void {
    window.history.back();
  }


  search(query: string): void {
    const { predicate } = this.sortState();
    if (query.toUpperCase() && predicate && MilitarComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(predicate)) {
      this.loadDefaultSortState();
    }
    this.page = 1;
    this.currentSearch = query.toUpperCase();
    this.navigateToWithComponentValues(this.sortState());
  }

  loadDefaultSortState(): void {
    this.sortState.set(this.sortService.parseSortParam(this.activatedRoute.snapshot.data[DEFAULT_SORT_DATA]));
  }

  delete(militar: IMilitar): void {
    const modalRef = this.modalService.open(MilitarDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.militar = militar;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => { this.load(); }),
      )
      .subscribe();
  }

  load(): void {      
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  loadEfetivo(): void {      
    this.isLoading = true;
    this.militarService.getAllMilitarsOM().pipe(
      concatMap(() => this.queryBackend())
    ).subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  onSaveFinalize(): void {
    this.isSaving = false;
  }

  onSaveSuccess(): void {
    this.previousState();
  }

  onSaveError(): void {
    // Api for inheritance.
  }
  
  subscribeToSaveResponse(result: Observable<HttpResponse<IMilitar>>): void {
    result.pipe(finalize(() => { this.onSaveFinalize(); })).subscribe({
      next: () => { this.onSaveSuccess(); },
      error: () => { this.onSaveError(); },
    });
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page, event, this.filters.filterOptions, this.currentSearch);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState(), this.filters.filterOptions, this.currentSearch);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    this.filters.initializeFromParams(params);
    if (params.has('search') && params.get('search') !== '') {
      this.currentSearch = params.get('search') as string;
      const { predicate } = this.sortState();
      if (predicate && MilitarComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(predicate)) {
        this.sortState.set({});
      }
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.militars = dataFromBody;
  }

  protected fillComponentAttributesFromResponseBody(data: IMilitar[] | null): IMilitar[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    const { page, filters, currentSearch } = this;

    this.isLoading = true;
    const pageToLoad: number = page;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      eagerload: true,
      query: currentSearch,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    filters.filterOptions.forEach(filterOption => {
      queryObject[filterOption.name] = filterOption.values;
    });
    if (this.currentSearch && this.currentSearch !== '') {
      return this.militarService.search(queryObject).pipe(tap(() => (this.isLoading = false)));
    }
    return this.militarService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page: number, sortState: SortState, filterOptions?: IFilterOption[], currentSearch?: string): void {
    const queryParamsObj: any = {
      search: currentSearch,
      page,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(sortState),
    };

    filterOptions?.forEach(filterOption => {
      queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
    });

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }

  protected loadRelationshipsOptions(): void {
    this.postoService
      .query()
      .pipe(map((res: HttpResponse<IPosto[]>) => res.body ?? []))
      .pipe(map((postos: IPosto[]) => this.postoService.addPostoToCollectionIfMissing<IPosto>(postos, this.militar?.posto)))
      .subscribe((postos: IPosto[]) => {
        this.postosSharedCollection = postos;
        //this.filteredPostos = postos; // Inicializa com todos os postos
    });
  }
}
