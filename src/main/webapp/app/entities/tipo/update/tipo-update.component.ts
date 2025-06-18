import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITipo } from '../tipo.model';
import { TipoService } from '../service/tipo.service';
import { TipoFormGroup, TipoFormService } from './tipo-form.service';

@Component({
  standalone: true,
  selector: 'jhi-tipo-update',
  templateUrl: './tipo-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TipoUpdateComponent implements OnInit {
  isSaving = false;
  tipo: ITipo | null = null;

  protected tipoService = inject(TipoService);
  protected tipoFormService = inject(TipoFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TipoFormGroup = this.tipoFormService.createTipoFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tipo }) => {
      this.tipo = tipo;
      if (tipo) {
        this.updateForm(tipo);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tipo = this.tipoFormService.getTipo(this.editForm);
    if (tipo.id !== null) {
      this.subscribeToSaveResponse(this.tipoService.update(tipo));
    } else {
      this.subscribeToSaveResponse(this.tipoService.create(tipo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITipo>>): void {
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

  protected updateForm(tipo: ITipo): void {
    this.tipo = tipo;
    this.tipoFormService.resetForm(this.editForm, tipo);
  }
}
