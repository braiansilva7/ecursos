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
import { EntityArrayResponseType, PostoService } from '../service/posto.service';
import { PostoDeleteDialogComponent } from '../delete/posto-delete-dialog.component';
import { IPosto } from '../posto.model';

@Component({
  standalone: true,
  selector: 'jhi-posto',
  templateUrl: './posto.component.html',
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
export class PostoComponent implements OnInit {
  private static readonly NOT_SORTABLE_FIELDS_AFTER_SEARCH = ['postoSigla', 'descricao'];

  subscription: Subscription | null = null;
  postos?: IPosto[];
  isLoading = false;

  sortState = sortStateSignal({});
  currentSearch = '';

  itemsPerPage = ITEMS_PER_PAGE;
  links: WritableSignal<Record<string, undefined | Record<string, string | undefined>>> = signal({});
  hasMorePage = computed(() => !!this.links().next);
  isFirstFetch = computed(() => Object.keys(this.links()).length === 0);

  public router = inject(Router);
  protected postoService = inject(PostoService);
  protected activatedRoute = inject(ActivatedRoute);
  protected sortService = inject(SortService);
  protected parseLinks = inject(ParseLinks);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (_index: number, item: IPosto): number => this.postoService.getPostoIdentifier(item);

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
    this.postos = [];
  }

  loadNextPage(): void {
    this.load();
  }

  search(query: string): void {
    const { predicate } = this.sortState();
    if (query.toUpperCase() && predicate && PostoComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(predicate)) {
      this.loadDefaultSortState();
    }
    this.currentSearch = query.toUpperCase();
    this.navigateToWithComponentValues(this.sortState());
  }

  loadDefaultSortState(): void {
    this.sortState.set(this.sortService.parseSortParam(this.activatedRoute.snapshot.data[DEFAULT_SORT_DATA]));
  }

  delete(posto: IPosto): void {
    const modalRef = this.modalService.open(PostoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.posto = posto;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => { this.load(); }),
      )
      .subscribe();
  }

  getOrgaoName(orgao: number | null | undefined): string {
    if (orgao == null) {
      return 'DESCONHECIDO'; // Retorna um valor padrão se `orgao` for `null` ou `undefined`
    }
    
    switch (orgao) {
      case 1:
        return 'MARINHA DO BRASIL';
      case 2:
        return 'EXÉRCITO BRASILEIRO';
      case 3:
        return 'FORÇA AÉREA BRASILEIRA';
      case 4:
        return 'POLÍCIA MILITAR';
      case 5:
        return 'CORPO DE BOMBEIROS';
      case 6:
        return 'ÓRGÃO CIVIL BRASILEIRO';
      case 7:
        return 'ÓRGÃO ESTRANGEIRO';
      default:
        return 'DESCONHECIDO';
    }
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
      if (predicate && PostoComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(predicate)) {
        this.sortState.set({});
      }
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.postos = dataFromBody;
  }

  protected fillComponentAttributesFromResponseBody(data: IPosto[] | null): IPosto[] {
    // If there is previous link, data is a infinite scroll pagination content.
    if (this.links().prev) {
      const postosNew = this.postos ?? [];
      if (data) {
        for (const d of data) {
          if (postosNew.some(op => op.id === d.id)) {
            postosNew.push(d);
          }
        }
      }
      return postosNew;
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
      return this.postoService.search(queryObject).pipe(tap(() => (this.isLoading = false)));
    }
    return this.postoService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
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
