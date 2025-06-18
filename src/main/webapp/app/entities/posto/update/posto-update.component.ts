import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPosto } from '../posto.model';
import { PostoService } from '../service/posto.service';
import { PostoFormGroup, PostoFormService } from './posto-form.service';

@Component({
  standalone: true,
  selector: 'jhi-posto-update',
  templateUrl: './posto-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PostoUpdateComponent implements OnInit {
  isSaving = false;
  posto: IPosto | null = null;

  protected postoService = inject(PostoService);
  protected postoFormService = inject(PostoFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PostoFormGroup = this.postoFormService.createPostoFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ posto }) => {
      this.posto = posto;
      if (posto) {
        this.updateForm(posto);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const posto = this.postoFormService.getPosto(this.editForm);
    if (posto.id !== null) {
      this.subscribeToSaveResponse(this.postoService.update(posto));
    } else {
      this.subscribeToSaveResponse(this.postoService.create(posto));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPosto>>): void {
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

  protected updateForm(posto: IPosto): void {
    this.posto = posto;
    this.postoFormService.resetForm(this.editForm, posto);
  }
}
