import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMilitar } from 'app/entities/militar/militar.model';
import { MilitarService } from 'app/entities/militar/service/militar.service';
import { ITurma } from 'app/entities/turma/turma.model';
import { TurmaService } from 'app/entities/turma/service/turma.service';
import { StatusEnum } from 'app/entities/enumerations/status-enum.model';
import { CapacitacaoService } from '../service/capacitacao.service';
import { ICapacitacao } from '../capacitacao.model';
import { CapacitacaoFormGroup, CapacitacaoFormService } from './capacitacao-form.service';
import dayjs, { Dayjs } from 'dayjs';
import { NgSelectModule } from '@ng-select/ng-select';

@Component({
  standalone: true,
  selector: 'jhi-capacitacao-update',
  templateUrl: './capacitacao-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, NgSelectModule],
})
export class CapacitacaoUpdateComponent implements OnInit {
  isSaving = false;
  capacitacao: ICapacitacao | null = null;
  statusEnumValues = Object.keys(StatusEnum);
  militarsSharedCollection: IMilitar[] = [];
  turmasSharedCollection: ITurma[] = [];
  selectedTurma: any = null;
  selectedMilitar: any = null;
  fotoUrl: string | null = null; // Caminho da foto
  availableSeats: number | null = null;
  initialTurmaId: number | null = null;

  protected capacitacaoService = inject(CapacitacaoService);
  protected capacitacaoFormService = inject(CapacitacaoFormService);
  protected militarService = inject(MilitarService);
  protected turmaService = inject(TurmaService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CapacitacaoFormGroup = this.capacitacaoFormService.createCapacitacaoFormGroup();

  compareMilitar = (o1: IMilitar | null, o2: IMilitar | null): boolean => this.militarService.compareMilitar(o1, o2);

  compareTurma = (o1: ITurma | null, o2: ITurma | null): boolean => this.turmaService.compareTurma(o1, o2);

  ngOnInit(): void {
    
    this.activatedRoute.data.subscribe(({ capacitacao }) => {
      this.capacitacao = capacitacao;
      if (capacitacao) {
        this.initialTurmaId = capacitacao.turma?.id ?? null;
        this.updateForm(capacitacao);
      }

      // Verificar o valor inicial de "turma" para edição
      const turmaValue = this.editForm.get('turma')?.value;
      if (turmaValue) {
        this.selectedTurma = this.turmasSharedCollection.find(
          (turma) => turma.id === turmaValue.id
        );
        this.onTurmaSelected(turmaValue?.id);

      }

      const selectedMilitar = this.editForm.get('militar')?.value;
      if (selectedMilitar) {
        this.selectedMilitar = this.militarsSharedCollection.find(
          (militar) => militar.id === selectedMilitar.id
        );

        this.onGetFoto(selectedMilitar.saram);      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  onTurmaChange(): void {
    // Acesse diretamente o valor selecionado no formControlName
    const selectedTurma = this.editForm.get('turma')?.value;
    
    this.selectedTurma = selectedTurma ?? null;

    this.onTurmaSelected(selectedTurma?.id);
  }

  onTurmaSelected(turmaId: number | undefined): void {  
    if (turmaId !== undefined) {
      this.turmaService
        .countAvailableSeats(turmaId)
        .subscribe({
          next: seats => {
            this.availableSeats = seats > 0 ? seats : null;
          },
          error(err) {
            console.error('Erro ao buscar vagas disponíveis:', err);
          }
        });
    }
  }

  // capactacao-update.component.ts
  get isNew(): boolean {
    // se não existir capacitacao ou o id for nulo, é criação
    return this.capacitacao?.id == null;
  }

  get turmaChanged(): boolean {
    const turma = this.editForm.get('turma')?.value;
    return !!(
      this.initialTurmaId !== null &&
      turma?.id !== this.initialTurmaId
    );
  }  

  onGetFoto(saram: any): void {
    //Verifica se o valor foi encontrado antes de continuar
    if (saram) {
      this.militarService.getFotoApiLocal(saram).subscribe({
        next: (url) => {
          // Use diretamente a URL retornada pela API
          this.fotoUrl = url;

          // Faça uma verificação adicional para garantir que o recurso exista
          this.checkImage(url).then((exists) => {
            if (!exists) {
              console.warn('Imagem não encontrada no servidor:', url);
              this.fotoUrl = null; // Ou defina uma imagem padrão
            }
          });
        },
        error: (err) => {
          console.error('Erro ao carregar foto:', err);
          this.fotoUrl = null; // Define como nulo ou imagem padrão
        },
      });
    } else {
      console.warn('Militar ou SARAM não definido.');
    }
  }

  onMilitarChange(): void {
    // Acesse o valor do FormControl diretamente
    const selectedMilitar = this.editForm.get('militar')?.value;
  
    if (selectedMilitar) {
      this.selectedMilitar = selectedMilitar;

      this.onGetFoto(selectedMilitar.saram);
    } else {
      this.selectedMilitar = null; // Reseta se nada for selecionado
    }
  }

  checkImage(url: string): Promise<boolean> {
    return new Promise((resolve) => {
      const img = new Image();
      img.onload = () => resolve(true);
      img.onerror = () => resolve(false);
      img.src = url;
    });
  }

  getFormattedDate(inicio: Dayjs | string | null | undefined): string {
    if (inicio) {
      if (dayjs.isDayjs(inicio)) {
        return inicio.format('DD/MM/YYYY');
      }
      return dayjs(inicio).format('DD/MM/YYYY');
    }
    return 'NULL';
  }

  getFormattedStatus(status: string): string {
    const STATUS_MAP: Record<string, string> = {
      AGUARDANDO_APROVACAO: "AGUARDANDO APROVAÇÃO (DTI/COMGAP)",
      AGUARDANDO_BCA_APROVACAO: "AGUARDANDO BCA DE APROVAÇÃO",
      CANCELADO: "CANCELADO",
      CONCLUIDO: "CONCLUÍDO",
      EM_ANDAMENTO: "EM ANDAMENTO",
      INDICACAO_NAO_APROVADA_PELA_ORGANIZACAO_DO_CURSO:
        "INDICAÇÃO NÃO APROVADA PELA ORGANIZAÇÃO DO CURSO",
    };
  
    return STATUS_MAP[status] || status;
  }

  save(): void {
    this.isSaving = true;
    
    const capacitacao = this.capacitacaoFormService.getCapacitacao(this.editForm);

    if (capacitacao.id !== null) {
      this.subscribeToSaveResponse(this.capacitacaoService.update(capacitacao));
    } else {
      this.subscribeToSaveResponse(this.capacitacaoService.create(capacitacao));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICapacitacao>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(capacitacao: ICapacitacao): void {
    this.capacitacao = capacitacao;
    this.capacitacaoFormService.resetForm(this.editForm, capacitacao);

    this.militarsSharedCollection = this.militarService.addMilitarToCollectionIfMissing<IMilitar>(
      this.militarsSharedCollection,
      capacitacao.militar,
    );
    this.turmasSharedCollection = this.turmaService.addTurmaToCollectionIfMissing<ITurma>(this.turmasSharedCollection, capacitacao.turma);
  }

  protected loadRelationshipsOptions(): void {
    this.militarService
      .queryAll()
      .pipe(map((res: HttpResponse<IMilitar[]>) => res.body ?? []))
      .pipe(
        map((militars: IMilitar[]) => this.militarService.addMilitarToCollectionIfMissing<IMilitar>(militars, this.capacitacao?.militar)),
      )
      .subscribe((militars: IMilitar[]) => (this.militarsSharedCollection = militars));

    this.turmaService
      .queryAll()
      .pipe(map((res: HttpResponse<ITurma[]>) => res.body ?? []))
      .pipe(map((turmas: ITurma[]) => this.turmaService.addTurmaToCollectionIfMissing<ITurma>(turmas, this.capacitacao?.turma)))
      .subscribe((turmas: ITurma[]) => (this.turmasSharedCollection = turmas));
  }
}
