import { Component, inject, OnInit } from '@angular/core';
import { IMilitar } from '../../entities/militar/militar.model';
import { HttpResponse } from '@angular/common/http';
import { ICapacitacao } from '../../entities/capacitacao/capacitacao.model';
import { MilitarService } from '../../entities/militar/service/militar.service';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { map } from 'rxjs/operators';
import { CapacitacaoService } from '../../entities/capacitacao/service/capacitacao.service';
import { NgChartsModule } from 'ng2-charts';
import { ChartData, ChartType, Chart } from 'chart.js';
import ChartDataLabels from 'chartjs-plugin-datalabels';
import SharedModule from 'app/shared/shared.module';
import dayjs, { Dayjs } from 'dayjs';

// Registrar o plugin
Chart.register(ChartDataLabels);

@Component({
  selector: 'jhi-dashboard',
  standalone: true,
  imports: [RouterModule, NgChartsModule, SharedModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export default class DashboardComponent implements OnInit {

  // Propriedades da classe
  capacitacao: ICapacitacao | null = null;
  militarsSharedCollection: IMilitar[] = [];
  capacitacaoSharedCollection: ICapacitacao[] = [];
  capacitacaoFimCollection: ICapacitacao[] = [];
  capacitacaoCollection: ICapacitacao[] = [];

  totalEmAndamento = 0;
  totalConcluido = 0;
  totalReprovado = 0;
  totalIndicado = 0;
  totalAprovado = 0;
  totalMatriculado = 0;

  today: Date = new Date();

  // Propriedades do gráfico (antes dos protected)
  public pieChartLabels: string[] = ['Em Andamento', 'Concluído', 'Reprovado', 'Indicado', 'Aprovado'];
  public pieChartType: ChartType = 'pie';

  public pieChartData: ChartData<'pie', number[], string> = {
    labels: this.pieChartLabels,
    datasets: [
      {
        data: [],
        backgroundColor: [
          'rgba(54,162,235,0.8)',   // Em Andamento
          'rgba(255,206,86,0.8)',   // Concluído
          'rgba(255,99,132,0.8)',   // Reprovado
          'rgba(0,128,0,0.8)',       // Indicado
          'rgba(0,139,139,1)'       // Aprovado
        ]
      }
    ]
  };

  public pieChartOptions: any = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top'
      },
      datalabels: {
        color: '#fff',
        font: {
          weight: 'bold'
        },
        formatter: (value: number) => `${value}`, // Exibe o valor numérico
      }
    }
  };

  // Injeção de serviços - protegidos
  protected militarService = inject(MilitarService);
  protected capacitacaoService = inject(CapacitacaoService);
  protected activatedRoute = inject(ActivatedRoute);

  // Métodos públicos antes de todos os protegidos/privados
  public ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ capacitacao }) => {
      this.capacitacao = capacitacao;
      this.loadRelationshipsOptions();
    });
  }

  formatDate(value: string | Date | Dayjs | null | undefined): string {
    if (!value){
      return ''; // Retorna string vazia se o valor for null/undefined
    } 
      
    if (dayjs.isDayjs(value)) {
      return value.format('DD/MM/YYYY'); // Se for Dayjs, formata usando o método `format`
    }
    return new Date(value).toLocaleDateString('pt-BR'); // Caso contrário, usa `Date`
  }

  public getBadgeClass(startDate: Date | null): string {
    if (!startDate) {
      return 'badge-secondary';
    }

    const diffDays = Math.ceil((startDate.getTime() - this.today.getTime()) / (1000 * 60 * 60 * 24));

    // Se a data de início é hoje, não exibe nenhuma badge
    if (diffDays === 0) {
      return 'badge-secondary';
    }

    // badge-danger: se o curso está a até 3 dias da data atual
    if (diffDays > 0 && diffDays <= 3) {
      return 'badge-danger';
    }

    // badge-warning: se o curso está entre 4 e 10 dias da data atual
    if (diffDays > 3 && diffDays <= 10) {
      return 'badge-warning';
    }

    // badge-success: caso contrário
    return 'badge-success';
  }

  public convertDayjsToDate(inicio: any): Date | null {
    if (!inicio) {
      return null;
    }

    // Verifica se o objeto é do tipo Dayjs (tem a propriedade $d)
    if (typeof inicio === 'object' && '$d' in inicio) {
      return new Date(inicio.$d); // Retorna a data do objeto Dayjs
    }

    // Caso seja uma string ou Date, converte diretamente
    return new Date(inicio);
  }

  // Métodos protegidos
  protected loadRelationshipsOptions(): void {
    this.militarService
      .queryAll()
      .pipe(map((res: HttpResponse<IMilitar[]>) => res.body ?? []))
      .pipe(
        map((militars: IMilitar[]) => this.militarService.addMilitarToCollectionIfMissing<IMilitar>(militars, this.capacitacao?.militar)),
      )
      .subscribe((militars: IMilitar[]) => (this.militarsSharedCollection = militars));

    this.capacitacaoService
      .queryAll()
      .pipe(map((res: HttpResponse<ICapacitacao[]>) => res.body ?? []))
      .pipe(
        map((capacitacoes: ICapacitacao[]) => {
          this.capacitacaoSharedCollection = Array.from(
            this.capacitacaoService
              .addCapacitacaoToCollectionIfMissing<ICapacitacao>(
                capacitacoes,
                this.capacitacao?.militar
              )
              .filter((capacitacao) => {
                const startDate = capacitacao.turma?.inicio ? this.convertDayjsToDate(capacitacao.turma.inicio) : null;
                if (!startDate) {
                  return false;
                }

                const diffDays = Math.ceil((startDate.getTime() - this.today.getTime()) / (1000 * 60 * 60 * 24));

                // Apenas cursos que começam em até 10 dias e não são no dia atual
                return diffDays > 0 && diffDays <= 10;
              })
              .reduce((acc, capacitacao) => {
                const ano = capacitacao.turma?.ano;
                const cursoNome = capacitacao.turma?.curso?.cursoNome;
                const chave = `${ano}-${cursoNome}`;

                if (!acc.has(chave)) {
                  acc.set(chave, capacitacao);
                }

                return acc;
              }, new Map<string, ICapacitacao>())
              .values()
          ).map((capacitacao) => ({
            ...capacitacao,
            badgeClass: this.getBadgeClass(
              capacitacao.turma?.inicio ? this.convertDayjsToDate(capacitacao.turma.inicio) : null
            ),
          })).sort((a, b) => {
            const dateA = a.turma?.inicio ? this.convertDayjsToDate(a.turma.inicio)?.getTime() ?? 0 : 0;
            const dateB = b.turma?.inicio ? this.convertDayjsToDate(b.turma.inicio)?.getTime() ?? 0 : 0;

            return dateA - dateB; // Ascendente (mais próxima primeiro)
          });

          this.capacitacaoFimCollection = Array.from(
            this.capacitacaoService
              .addCapacitacaoToCollectionIfMissing<ICapacitacao>(
                capacitacoes,
                this.capacitacao?.militar
              )
              .filter(capacitacao => {
                const endDate = capacitacao.turma?.termino ? this.convertDayjsToDate(capacitacao.turma.termino) : null;
                if (!endDate) {
                  return false;
                }
                const diffDays = Math.ceil((endDate.getTime() - this.today.getTime()) / (1000 * 60 * 60 * 24));
                return diffDays > 0 && diffDays <= 7;
              })
              .reduce((acc, capacitacao) => {
                const ano = capacitacao.turma?.ano;
                const cursoNome = capacitacao.turma?.curso?.cursoNome;
                const chave = `${ano}-${cursoNome}`;
                if (!acc.has(chave)) {
                  acc.set(chave, capacitacao);
                }
                return acc;
              }, new Map<string, ICapacitacao>())
              .values()
          ).map(capacitacao => ({
            ...capacitacao,
            badgeClass: this.getBadgeClass(
              capacitacao.turma?.termino ? this.convertDayjsToDate(capacitacao.turma.termino) : null
            ),
          })).sort((a, b) => {
            const dateA = a.turma?.termino ? this.convertDayjsToDate(a.turma.termino)?.getTime() ?? 0 : 0;
            const dateB = b.turma?.termino ? this.convertDayjsToDate(b.turma.termino)?.getTime() ?? 0 : 0;
            return dateA - dateB;
          });

          this.capacitacaoCollection = this.capacitacaoService.addCapacitacaoToCollectionIfMissing<ICapacitacao>(
            capacitacoes,
            this.capacitacao?.militar
          );
          
          // Filtrar e contar status diferentes
          this.totalEmAndamento = this.capacitacaoCollection.filter(
            (capacitacao) => capacitacao.capacitacaoStatus === 'EM_ANDAMENTO'
          ).length;
          
          this.totalAprovado = this.capacitacaoCollection.filter(
            (capacitacao) => capacitacao.capacitacaoStatus === 'APROVADO'
          ).length;

          this.totalConcluido = this.capacitacaoCollection.filter(
            (capacitacao) => capacitacao.capacitacaoStatus === 'CONCLUIDO'
          ).length;
          
          this.totalReprovado = this.capacitacaoCollection.filter(
            (capacitacao) => capacitacao.capacitacaoStatus === 'REPROVADO'
          ).length;
          
          this.totalIndicado = this.capacitacaoCollection.filter(
            (capacitacao) => capacitacao.capacitacaoStatus === 'INDICADO'
          ).length;

          this.totalMatriculado = this.capacitacaoCollection.filter(
            (capacitacao) => capacitacao.capacitacaoStatus === 'MATRICULADO'
          ).length;

          // Atualiza os dados do gráfico com os totais carregados
          this.updateChartData();
        })
      )
      .subscribe();
  }

  // Métodos privados
  private updateChartData(): void {
    // Criar uma nova cópia de `pieChartData` para forçar a atualização
    this.pieChartData = {
      labels: this.pieChartLabels,
      datasets: [
        {
          data: [
            this.totalEmAndamento,
            this.totalConcluido,
            this.totalReprovado,
            this.totalIndicado,
            this.totalAprovado
          ],
          backgroundColor: [
            'rgba(0,128,0,0.8)',   // Em Andamento
            'rgba(255,206,86,0.8)',   // Concluído
            'rgba(255,99,132,0.8)', // Reprovado
            'rgba(54,162,235,0.8)', // Indicado
            'rgba(0,139,139,1)'       // Aprovado
          ]
        }
      ]
    };
  }
}


