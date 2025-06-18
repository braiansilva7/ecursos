import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICurso } from 'app/entities/curso/curso.model';
import { CursoService } from 'app/entities/curso/service/curso.service';
import { StatusCursoEnum } from 'app/entities/enumerations/status-curso-enum.model';
import { ModalidadeEnum } from 'app/entities/enumerations/modalidade-enum.model';
import { TurmaService } from '../service/turma.service';
import { ITurma } from '../turma.model';
import { TurmaFormGroup, TurmaFormService } from './turma-form.service';
import { NgSelectModule } from '@ng-select/ng-select';

@Component({
  standalone: true,
  selector: 'jhi-turma-update',
  templateUrl: './turma-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, NgSelectModule],
})
export class TurmaUpdateComponent implements OnInit {
  isSaving = false;
  turma: ITurma | null = null;
  statusCursoEnumValues = Object.keys(StatusCursoEnum);
  modalidadeEnumValues = Object.keys(ModalidadeEnum);
  selectedCurso: any = null;
  cursosSharedCollection: ICurso[] = [];

  protected turmaService = inject(TurmaService);
  protected turmaFormService = inject(TurmaFormService);
  protected cursoService = inject(CursoService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TurmaFormGroup = this.turmaFormService.createTurmaFormGroup();

  compareCurso = (o1: ICurso | null, o2: ICurso | null): boolean => this.cursoService.compareCurso(o1, o2);

  ngOnInit(): void {
    
    this.activatedRoute.data.subscribe(({ turma }) => {
      this.turma = turma;
      if (turma) {
        this.updateForm(turma);
      }

      this.loadRelationshipsOptions();
    });

  }

  previousState(): void {
    window.history.back();
  }

  onCursoChange(): void {
    // Acesse o valor do FormControl diretamente
    const selectedCurso = this.editForm.get('curso')?.value;
      
    this.selectedCurso = selectedCurso ?? null;
  }

  save(): void {
    this.isSaving = true;
    const turma = this.turmaFormService.getTurma(this.editForm);
    if (turma.id !== null) {
      this.subscribeToSaveResponse(this.turmaService.update(turma));
    } else {
      this.subscribeToSaveResponse(this.turmaService.create(turma));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITurma>>): void {
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

  protected updateForm(turma: ITurma): void {
    this.turma = turma;
    this.turmaFormService.resetForm(this.editForm, turma);

    this.cursosSharedCollection = this.cursoService.addCursoToCollectionIfMissing<ICurso>(this.cursosSharedCollection, turma.curso);
  }

  protected loadRelationshipsOptions(): void {
    this.cursoService
      .queryAll()
      .pipe(map((res: HttpResponse<ICurso[]>) => res.body ?? []))
      .pipe(map((cursos: ICurso[]) => this.cursoService.addCursoToCollectionIfMissing<ICurso>(cursos, this.turma?.curso)))
      .subscribe((cursos: ICurso[]) => (this.cursosSharedCollection = cursos));
  }
  
  
}
