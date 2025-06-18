import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITipo } from 'app/entities/tipo/tipo.model';
import { TipoService } from 'app/entities/tipo/service/tipo.service';
import { ICurso } from '../curso.model';
import { CursoService } from '../service/curso.service';
import { CursoFormGroup, CursoFormService } from './curso-form.service';

@Component({
  standalone: true,
  selector: 'jhi-curso-update',
  templateUrl: './curso-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CursoUpdateComponent implements OnInit {
  isSaving = false;
  curso: ICurso | null = null;

  tiposSharedCollection: ITipo[] = [];

  protected cursoService = inject(CursoService);
  protected cursoFormService = inject(CursoFormService);
  protected tipoService = inject(TipoService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CursoFormGroup = this.cursoFormService.createCursoFormGroup();

  compareTipo = (o1: ITipo | null, o2: ITipo | null): boolean => this.tipoService.compareTipo(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ curso }) => {
      this.curso = curso;
      if (curso) {
        this.updateForm(curso);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const curso = this.cursoFormService.getCurso(this.editForm);
    if (curso.id !== null) {
      this.subscribeToSaveResponse(this.cursoService.update(curso));
    } else {
      this.subscribeToSaveResponse(this.cursoService.create(curso));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICurso>>): void {
    result.pipe(finalize(() => { this.onSaveFinalize(); })).subscribe({
      next: () => { this.onSaveSuccess(); },
      error: () => { this.onSaveError(); },
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

  protected updateForm(curso: ICurso): void {
    this.curso = curso;
    this.cursoFormService.resetForm(this.editForm, curso);

    this.tiposSharedCollection = this.tipoService.addTipoToCollectionIfMissing<ITipo>(this.tiposSharedCollection, curso.tipo);
  }

  protected loadRelationshipsOptions(): void {
    this.tipoService
      .query()
      .pipe(map((res: HttpResponse<ITipo[]>) => res.body ?? []))
      .pipe(map((tipos: ITipo[]) => this.tipoService.addTipoToCollectionIfMissing<ITipo>(tipos, this.curso?.tipo)))
      .subscribe((tipos: ITipo[]) => (this.tiposSharedCollection = tipos));
  }
}
