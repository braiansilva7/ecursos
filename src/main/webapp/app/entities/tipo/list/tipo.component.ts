import { Component, NgZone, OnInit, WritableSignal, computed, inject, signal } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { InfiniteScrollDirective } from 'ngx-infinite-scroll';
import { EntityArrayResponseType, TipoService } from '../service/tipo.service';
import { TipoDeleteDialogComponent } from '../delete/tipo-delete-dialog.component';
import { ITipo } from '../tipo.model';

@Component({
  standalone: true,
  selector: 'jhi-tipo',
  templateUrl: './tipo.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    InfiniteScrollDirective,
  ],
})
export class TipoComponent implements OnInit {
  private static readonly NOT_SORTABLE_FIELDS_AFTER_SEARCH = ['categoria'];

  subscription: Subscription | null = null;
  tipos?: ITipo[];
  isLoading = false;

  sortState = sortStateSignal({});
  currentSearch = '';

  itemsPerPage = ITEMS_PER_PAGE;
  links: WritableSignal<Record<string, undefined | Record<string, string | undefined>>> = signal({});
  hasMorePage = computed(() => !!this.links().next);
  isFirstFetch = computed(() => Object.keys(this.links()).length === 0);

  public router = inject(Router);
  protected tipoService = inject(TipoService);
  protected activatedRoute = inject(ActivatedRoute);
  protected sortService = inject(SortService);
  protected parseLinks = inject(ParseLinks);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (_index: number, item: ITipo): number => this.tipoService.getTipoIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => { this.fillComponentAttributeFromRoute(params, data); }),
        tap(() => { this.reset(); }),
        tap(() => { this.load(); }),
      )
      .subscribe();
  }

  reset(): void {
    this.tipos = [];
  }

  loadNextPage(): void {
    this.load();
  }

  search(query: string): void {
    const { predicate } = this.sortState();
    if (query.toUpperCase() && predicate && TipoComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(predicate)) {
      this.loadDefaultSortState();
    }
    this.currentSearch = query.toUpperCase();
    this.navigateToWithComponentValues(this.sortState());
  }

  loadDefaultSortState(): void {
    this.sortState.set(this.sortService.parseSortParam(this.activatedRoute.snapshot.data[DEFAULT_SORT_DATA]));
  }

  delete(tipo: ITipo): void {
    const modalRef = this.modalService.open(TipoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.tipo = tipo;
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

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event, this.currentSearch);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    if (params.has('search') && params.get('search') !== '') {
      this.currentSearch = params.get('search') as string;
      const { predicate } = this.sortState();
      if (predicate && TipoComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(predicate)) {
        this.sortState.set({});
      }
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.tipos = dataFromBody;
  }

  protected fillComponentAttributesFromResponseBody(data: ITipo[] | null): ITipo[] {
    // If there is previous link, data is a infinite scroll pagination content.
    if (this.links().prev) {
      const tiposNew = this.tipos ?? [];
      if (data) {
        for (const d of data) {
          if (tiposNew.some(op => op.id === d.id)) {
            tiposNew.push(d);
          }
        }
      }
      return tiposNew;
    }
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    const linkHeader = headers.get('link');
    if (linkHeader) {
      this.links.set(this.parseLinks.parseAll(linkHeader));
    } else {
      this.links.set({});
    }
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    const { currentSearch } = this;

    this.isLoading = true;
    const queryObject: any = {
      size: this.itemsPerPage,
      query: currentSearch,
    };
    if (this.hasMorePage()) {
      Object.assign(queryObject, this.links().next);
    } else if (this.isFirstFetch()) {
      Object.assign(queryObject, { sort: this.sortService.buildSortParam(this.sortState()) });
    }

    if (this.currentSearch && this.currentSearch !== '') {
      return this.tipoService.search(queryObject).pipe(tap(() => (this.isLoading = false)));
    }
    return this.tipoService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(sortState: SortState, currentSearch?: string): void {
    this.links.set({});

    const queryParamsObj = {
      search: currentSearch,
      sort: this.sortService.buildSortParam(sortState),
    };

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }
}
