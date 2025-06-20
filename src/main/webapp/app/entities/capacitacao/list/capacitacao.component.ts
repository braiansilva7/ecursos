import { Component, NgZone, OnInit, inject } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap, map } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ItemCountComponent } from 'app/shared/pagination';
import { FormsModule } from '@angular/forms';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { FilterComponent, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { ICapacitacao } from '../capacitacao.model';
import { StatusEnum } from 'app/entities/enumerations/status-enum.model';
import dayjs from 'dayjs';
import { CapacitacaoService, EntityArrayResponseType } from '../service/capacitacao.service';
import { CapacitacaoDeleteDialogComponent } from '../delete/capacitacao-delete-dialog.component';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';
import { NgSelectModule } from '@ng-select/ng-select';
import { IPosto } from 'app/entities/posto/posto.model';
import { PostoService } from 'app/entities/posto/service/posto.service';

@Component({
  standalone: true,
  selector: 'jhi-capacitacao',
  templateUrl: './capacitacao.component.html',
  imports: [
    RouterModule,
    FormsModule,
    NgSelectModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    FilterComponent,
    ItemCountComponent,
    HasAnyAuthorityDirective
  ],
})
export class CapacitacaoComponent implements OnInit {
  private static readonly NOT_SORTABLE_FIELDS_AFTER_SEARCH = ['capacitacaoStatus', 'sigpes'];

  subscription: Subscription | null = null;
  capacitacaos?: ICapacitacao[];
  isLoading = false;

  sortState = sortStateSignal({});
  statusFilter: string | null = null;
  currentSearch = '';
  filters: IFilterOptions = new FilterOptions();

  statusEnumValues = Object.keys(StatusEnum);
  showAdvanced = false;
  allCapacitacaos: ICapacitacao[] = [];
  nomeGuerraOptions: string[] = [];
  postoOptions: string[] = [];
  cursoSiglaOptions: string[] = [];
  turmaOptions: string[] = [];
  omOptions: string[] = [];
  categoriaOptions: string[] = [];

  filteredCapacitacaos: ICapacitacao[] = [];
  advancedFiltersActive = false;
  advancedFilters: any = {
    nomeGuerra: '',
    posto: '',
    ano: '',
    turma: '',
    om: '',
    cursoSigla: '',
    categoria: '',
    inicio: '',
    termino: '',
    capacitacaoStatus: ''
  };

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  public router = inject(Router);
  protected capacitacaoService = inject(CapacitacaoService);
  protected activatedRoute = inject(ActivatedRoute);
  protected sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);
  protected postoService = inject(PostoService);

  private postoPriorityMap: Map<string, number> = new Map();

  trackId = (_index: number, item: ICapacitacao): number => this.capacitacaoService.getCapacitacaoIdentifier(item);

  toggleAdvancedFilters(): void {
    this.showAdvanced = !this.showAdvanced;
    if (this.showAdvanced && this.allCapacitacaos.length === 0) {
      this.loadAllCapacitacaos();
    }
  }

  ngOnInit(): void {
    const queryParams = this.activatedRoute.snapshot.queryParamMap;
    const skipInitialLoad = queryParams.has('turma') || queryParams.has('ano');

    this.activatedRoute.queryParams.subscribe((params: any) => {
      if (params['status']) {
        this.showAdvanced = true;
        this.advancedFilters.capacitacaoStatus = params['status'];
      }

      if (params['turma']) {
        this.showAdvanced = true;
        this.advancedFilters.turma = params['turma'];
      }

      if (params['ano']) {
        this.showAdvanced = true;
        this.advancedFilters.ano = params['ano'];
      }
    });

    // Carrega todos os Postos para mapear a prioridade e ordenar o filtro
    this.postoService
      .query()
      .pipe(map((res: HttpResponse<IPosto[]>) => res.body ?? []))
      .subscribe(postos => {
        this.postoPriorityMap.clear();
        postos.forEach(p => {
          if (p.postoSigla) {
            this.postoPriorityMap.set(p.postoSigla, p.prioridade ?? 0);
          }
        });
      });

    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => {
          this.fillComponentAttributeFromRoute(params, data);
        }),
        tap(() => {
          if (!skipInitialLoad) {
            this.load();
          }
        }),
      )
      .subscribe();

    this.loadAllCapacitacaos(true);

    this.filters.filterChanges.subscribe(filterOptions => this.handleNavigation(1, this.sortState(), filterOptions));
  }

  generatePDF(): void {
    const doc = new jsPDF('landscape');
    
    // Adiciona o título com a cor especificada
    doc.setFontSize(16);
    doc.text('Relatório de Capacitação', 148, 10, { align: 'center' }); // Ajusta a posição para centralizar em modo paisagem
    
    const columns = [
      { header: 'Início', dataKey: 'inicio' },
      { header: 'Término', dataKey: 'termino' },
      { header: 'Ano', dataKey: 'ano' },
      { header: 'Status', dataKey: 'capacitacaoStatus' },
      { header: 'N. BCA', dataKey: 'numeroBca' },
      { header: 'OM', dataKey: 'om' },
      { header: 'Posto', dataKey: 'posto' },
      { header: 'Militar', dataKey: 'nomeCompleto' },
      { header: 'Nome de Guerra', dataKey: 'nomeGuerra' },
      { header: 'E-mail', dataKey: 'email' },
      { header: 'Categoria', dataKey: 'tipo' },
      { header: 'Curso', dataKey: 'cursoNome' },
      { header: 'SIGPES', dataKey: 'sigpes' },
    ];

    // Função para formatar o valor de capacitacaoStatus
    const formatStatus = (status: string | null | undefined): string => {
      if (!status) {
        return '';
      }
      switch (status) {
        case 'EM_ANDAMENTO':
          return 'EM ANDAMENTO';
        case 'INDICACAO_NAO_APROVADA_PELA_ORGANIZACAO_DO_CURSO':
          return 'INDICAÇÃO NÃO APROVADA';
        default:
          return status.replace(/_/g, ' '); // Substitui underscores por espaços
      }
    };

    const rows = this.capacitacaos?.map(capacitacao => ({
      inicio: capacitacao?.turma?.inicio ? capacitacao.turma?.inicio.format('DD/MM/YYYY') : '',
      termino: capacitacao.turma?.termino ? capacitacao.turma?.termino.format('DD/MM/YYYY') : '',
      ano: capacitacao.turma?.ano ?? '',
      capacitacaoStatus: formatStatus(capacitacao.capacitacaoStatus),
      numeroBca: capacitacao.turma?.numeroBca ?? '',
      om: capacitacao.militar?.om ?? '',
      posto: capacitacao.militar?.posto?.postoSigla ?? '',
      nomeCompleto: capacitacao.militar?.nomeCompleto ?? '',
      nomeGuerra: capacitacao.militar?.nomeGuerra ?? '',
      email: capacitacao.militar?.email ?? '',
      tipo: capacitacao.turma?.curso?.tipo?.categoria ?? '',
      cursoNome: capacitacao.turma?.curso?.cursoNome ?? '',
      sigpes: capacitacao.sigpes ?? '',
    }));

    autoTable(doc, {
      columns,
      body: rows,
      startY: 20, // Define a posição vertical para começar a tabela abaixo do título
      styles: { fontSize: 8 },
      theme: 'grid',
      headStyles: { fillColor: '#1d52c2', textColor: '#FFFFFF' }, // Cor do cabeçalho da tabela
      alternateRowStyles: { fillColor: '#f2f2f2' }, // Cor alternada para linhas da tabela
    });

    doc.save('Capacitacao.pdf');
  }

  generateExcel(): void {
    const formatStatus = (status: string | null | undefined): string => {
      if (!status) {
        return '';
      }
      switch (status) {
        case 'EM_ANDAMENTO':
          return 'EM ANDAMENTO';
        case 'INDICACAO_NAO_APROVADA_PELA_ORGANIZACAO_DO_CURSO':
          return 'INDICAÇÃO NÃO APROVADA';
        default:
          return status.replace(/_/g, ' ');
      }
    };

    const headers = [
      'Início',
      'Término',
      'Ano',
      'Status',
      'N. BCA',
      'OM',
      'Posto',
      'Militar',
      'Nome de Guerra',
      'E-mail',
      'Categoria',
      'Curso',
      'SIGPES',
    ];

    const rows = this.capacitacaos?.map(c => [
      c.turma?.inicio ? this.getFormattedDate(c.turma.inicio) : '',
      c.turma?.termino ? this.getFormattedDate(c.turma.termino) : '',
      c.turma?.ano ?? '',
      formatStatus(c.capacitacaoStatus),
      c.turma?.numeroBca ?? '',
      c.militar?.om ?? '',
      c.militar?.posto?.postoSigla ?? '',
      c.militar?.nomeCompleto ?? '',
      c.militar?.nomeGuerra ?? '',
      c.militar?.email ?? '',
      c.turma?.curso?.tipo?.categoria ?? '',
      c.turma?.curso?.cursoNome ?? '',
      c.sigpes ?? '',
    ]);

    const csvContent =
      headers.join(';') +
      '\n' +
      (rows ?? [])
        .map(r => r.map(v => `"${String(v).replace(/"/g, '""')}"`).join(';'))
        .join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'Capacitacao.csv';
    link.click();
  }

  getFormattedDate(inicio: any): string {
    if (inicio && dayjs.isDayjs(inicio)) {
      return inicio.toDate().toLocaleDateString('pt-BR');
    }
    return 'NULL';
  }

  search(query: string): void {
    const { predicate } = this.sortState();
    if (query.toUpperCase() && predicate && CapacitacaoComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(predicate)) {
      this.loadDefaultSortState();
    }
    
    this.page = 1;
    this.currentSearch = query.toUpperCase();
    this.navigateToWithComponentValues(this.sortState());
  }

  loadDefaultSortState(): void {
    this.sortState.set(this.sortService.parseSortParam(this.activatedRoute.snapshot.data[DEFAULT_SORT_DATA]));
  }

  delete(capacitacao: ICapacitacao): void {
    const modalRef = this.modalService.open(CapacitacaoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.capacitacao = capacitacao;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
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
    this.handleNavigation(this.page, event, this.filters.filterOptions, this.currentSearch);
  }

  navigateToPage(page: number): void {
    if (this.advancedFiltersActive) {
      this.page = page;
      this.updatePageData();
    } else {
      this.handleNavigation(page, this.sortState(), this.filters.filterOptions, this.currentSearch);
    }
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    this.filters.initializeFromParams(params);
    if (params.has('search') && params.get('search') !== '') {
      this.currentSearch = params.get('search') as string;
      const { predicate } = this.sortState();
      if (predicate && CapacitacaoComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(predicate)) {
        this.sortState.set({});
      }
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.capacitacaos = dataFromBody;
  }

  protected fillComponentAttributesFromResponseBody(data: ICapacitacao[] | null): ICapacitacao[] {
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
      return this.capacitacaoService.search(queryObject).pipe(tap(() => (this.isLoading = false)));
    }
    return this.capacitacaoService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  loadAllCapacitacaos(applyFilterAfterLoad: boolean = false): void {
    this.capacitacaoService.queryAll({ eagerload: true }).subscribe(res => {
      this.allCapacitacaos = res.body ?? [];
      this.updateFilterOptions();
      this.advancedFiltersActive = false;
      this.filteredCapacitacaos = [];
      this.page = 1;
      this.totalItems = this.allCapacitacaos.length;
      this.updatePageData();
      if (
        applyFilterAfterLoad &&
        (this.advancedFilters.turma || this.advancedFilters.ano || this.advancedFilters.capacitacaoStatus)
      ) {
        this.applyAdvancedFilters();
      }
    });
  }

  updateFilterOptions(): void {
    const nomeSet = new Set<string>();
    const postoSet = new Set<string>();
    const cursoSet = new Set<string>();
    const turmaSet = new Set<string>();
    const omSet = new Set<string>();
    const categoriaSet = new Set<string>();

    this.allCapacitacaos.forEach(c => {
      if (c.militar?.nomeGuerra) {
        nomeSet.add(c.militar.nomeGuerra);
      }
      if (c.militar?.posto?.postoSigla) {
        postoSet.add(c.militar.posto.postoSigla);
      }
      if (c.turma?.curso?.cursoSigla) {
        cursoSet.add(c.turma.curso.cursoSigla);
      }
      if (c.turma?.curso?.cursoNome) {
        turmaSet.add(c.turma.curso.cursoNome);
      }
      if (c.militar?.om) {
        omSet.add(c.militar.om);
      }
      if (c.turma?.curso?.tipo?.categoria) {
        categoriaSet.add(c.turma.curso.tipo.categoria);
      }
    });

    this.nomeGuerraOptions = Array.from(nomeSet).sort();
    this.postoOptions = Array.from(postoSet).sort((a, b) => {
      const pa = this.postoPriorityMap.get(a) ?? Number.MAX_SAFE_INTEGER;
      const pb = this.postoPriorityMap.get(b) ?? Number.MAX_SAFE_INTEGER;
      return pa - pb;
    });
    this.cursoSiglaOptions = Array.from(cursoSet).sort();
    this.turmaOptions = Array.from(turmaSet).sort();
    this.omOptions = Array.from(omSet).sort();
    this.categoriaOptions = Array.from(categoriaSet).sort();
  }

  applyAdvancedFilters(): void {
    const f = this.advancedFilters;
    this.filteredCapacitacaos = this.allCapacitacaos.filter(c => {
      return (
        (!f.nomeGuerra || (c.militar?.nomeGuerra && c.militar.nomeGuerra.toLowerCase().includes(f.nomeGuerra.toLowerCase()))) &&
        (!f.posto || (c.militar?.posto?.postoSigla && c.militar.posto.postoSigla.toLowerCase().includes(f.posto.toLowerCase()))) &&
        (!f.om || (c.militar?.om && c.militar.om.toLowerCase().includes(f.om.toLowerCase()))) &&
        (!f.cursoSigla || (c.turma?.curso?.cursoSigla && c.turma.curso.cursoSigla.toLowerCase().includes(f.cursoSigla.toLowerCase()))) &&
        (!f.categoria || (c.turma?.curso?.tipo?.categoria && c.turma.curso.tipo.categoria.toLowerCase().includes(f.categoria.toLowerCase()))) &&
        (!f.ano || String(c.turma?.ano ?? '').includes(f.ano)) &&
        (!f.turma || (c.turma?.curso?.cursoNome && c.turma.curso.cursoNome.toLowerCase().includes(f.turma.toLowerCase()))) &&
        (!f.capacitacaoStatus || c.capacitacaoStatus === f.capacitacaoStatus) &&
        (!f.inicio || (c.turma?.inicio && c.turma.inicio.format('YYYY-MM-DD') >= f.inicio)) &&
        (!f.termino || (c.turma?.termino && c.turma.termino.format('YYYY-MM-DD') <= f.termino))
      );
    });
    this.advancedFiltersActive = true;
    this.page = 1;
    this.totalItems = this.filteredCapacitacaos.length;
    this.updatePageData();
  }

  clearAdvancedFilters(): void {
    this.advancedFilters = {
      nomeGuerra: '',
      posto: '',
      ano: '',
      turma: '',
      om: '',
      cursoSigla: '',
      categoria: '',
      inicio: '',
      termino: '',
      capacitacaoStatus: ''
    };
    this.advancedFiltersActive = false;
    this.filteredCapacitacaos = [];
    this.page = 1;
    this.totalItems = this.allCapacitacaos.length;
    this.updatePageData();
  }

  updatePageData(): void {
    const source = this.advancedFiltersActive ? this.filteredCapacitacaos : this.allCapacitacaos;
    const start = (this.page - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    this.capacitacaos = source.slice(start, end);
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
}
