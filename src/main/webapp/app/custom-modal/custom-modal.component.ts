import { Component, Input, Output, EventEmitter, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { StatusEnum } from 'app/entities/enumerations/status-enum.model';
import { CapacitacaoService } from '../entities/capacitacao/service/capacitacao.service';
import { ICapacitacao } from '../entities/capacitacao/capacitacao.model';
import { CapacitacaoFormGroup, CapacitacaoFormService } from '../entities/capacitacao/update/capacitacao-form.service';
import { IMilitar } from 'app/entities/militar/militar.model';
import { MilitarService } from 'app/entities/militar/service/militar.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { finalize, map, Observable } from 'rxjs';
import { NgSelectModule } from '@ng-select/ng-select';
import { TurmaService } from 'app/entities/turma/service/turma.service';
import SharedModule from 'app/shared/shared.module';

@Component({
  selector: 'jhi-custom-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, TranslateModule, NgSelectModule, SharedModule],
  templateUrl: './custom-modal.component.html',
  styleUrls: ['./custom-modal.component.scss'],
})
export class CustomModalComponent implements OnInit{
  
  @Input() data: any = {}; // Dados enviados para o modal
  @Output() modalClose = new EventEmitter<string>();

  statusEnumValues = Object.keys(StatusEnum);
  capacitacao: ICapacitacao | null = null;
  militarsSharedCollection: IMilitar[] = [];
  selectedMilitares: any = null;
  isSaving = false;
  militarSelectedCount = 0;
  selectStatus = false;
  availableSeats: number | null = null;
  errorMessage: string | null = null;  // vai guardar a mensagem de erro

  protected capacitacaoService = inject(CapacitacaoService);
  protected capacitacaoFormService = inject(CapacitacaoFormService);
  protected militarService = inject(MilitarService);
  protected activatedRoute = inject(ActivatedRoute);
  protected turmaService = inject(TurmaService);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CapacitacaoFormGroup = this.capacitacaoFormService.createCapacitacaoFormGroup();

  compareMilitar = (o1: IMilitar | null, o2: IMilitar | null): boolean => this.militarService.compareMilitar(o1, o2);

  ngOnInit(): void {
    this.loadRelationshipsOptions();
    this.turmaService
      .countAvailableSeats(this.data.id)
      .subscribe({
        next: seats => {
          this.availableSeats = seats;
        },
        error(err) {
          console.error('Erro ao buscar vagas disponíveis:', err);
        }
    });
  }

  closeModal(): void {
    this.modalClose.emit('cancel');
  }

  updateMilitarCount(event: Event): void {
    if (!Array.isArray(event)) {
      console.warn("O evento recebido não é um array válido.");
      return;
    }

    this.militarSelectedCount = event.length;
  }

  updateStatusEnum(event: Event): void {
    const target = event.target as HTMLSelectElement;
    if (target.value) {
      this.selectStatus = true;
    }
  }

  previousState(): void {
    this.modalClose.emit('saved');
  }

  save(): void {
    this.isSaving = true;
    this.errorMessage = null;

    const capacitacao = this.capacitacaoFormService.getCapacitacao(this.editForm);

    if (this.data) {
      capacitacao.turma = this.data;
    }

    if (Array.isArray(capacitacao.militar) && capacitacao.militar.length > 0) {
      const militaresComDados = capacitacao.militar.map((militar: any) => ({
        militar,
        capacitacaoStatus: capacitacao.capacitacaoStatus,
        id: capacitacao.id,
        turma: capacitacao.turma,
      }));
    
      militaresComDados.forEach((militar: any) => {   
        if (militar.id !== null) {
          this.subscribeToSaveResponse(this.capacitacaoService.update(militar));
        } else {

          this.subscribeToSaveResponse(this.capacitacaoService.create(militar));
        }
      });
    
    } else {
      console.error("Nenhum militar encontrado na capacitação.");
    }

  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICapacitacao>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: (res: HttpErrorResponse) => this.onSaveError(res),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(response: HttpErrorResponse): void {
  
    this.errorMessage = (response.error?.message as string) || 'Erro desconhecido ao salvar.';
  
    this.isSaving = false;
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
  } 

  protected loadRelationshipsOptions(): void {
    this.militarService
      .queryAll()
      .pipe(map((res: HttpResponse<IMilitar[]>) => res.body ?? []))
      .pipe(
        map((militars: IMilitar[]) => this.militarService.addMilitarToCollectionIfMissing<IMilitar>(militars, this.capacitacao?.militar)),
      )
      .subscribe((militars: IMilitar[]) => (this.militarsSharedCollection = militars));

  }
}
