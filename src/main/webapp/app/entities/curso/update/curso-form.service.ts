import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICurso, NewCurso } from '../curso.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICurso for edit and NewCursoFormGroupInput for create.
 */
type CursoFormGroupInput = ICurso | PartialWithRequiredKeyOf<NewCurso>;

type CursoFormDefaults = Pick<NewCurso, 'id'>;

type CursoFormGroupContent = {
  id: FormControl<ICurso['id'] | NewCurso['id']>;
  cursoNome: FormControl<ICurso['cursoNome']>;
  cursoSigla: FormControl<ICurso['cursoSigla']>;
  empresa: FormControl<ICurso['empresa']>;
  tipo: FormControl<ICurso['tipo']>;
};

export type CursoFormGroup = FormGroup<CursoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CursoFormService {
  createCursoFormGroup(curso: CursoFormGroupInput = { id: null }): CursoFormGroup {
    const cursoRawValue = {
      ...this.getFormDefaults(),
      ...curso,
    };
    return new FormGroup<CursoFormGroupContent>({
      id: new FormControl(
        { value: cursoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      cursoNome: new FormControl(cursoRawValue.cursoNome, {
        validators: [Validators.required],
      }),
      cursoSigla: new FormControl(cursoRawValue.cursoSigla, {
        validators: [Validators.required],
      }),
      empresa: new FormControl(cursoRawValue.empresa, {
        validators: [Validators.required],
      }),
      tipo: new FormControl(cursoRawValue.tipo, {
        validators: [Validators.required],
      }),
    });
  }

  getCurso(form: CursoFormGroup): ICurso | NewCurso {
    return form.getRawValue() as ICurso | NewCurso;
  }

  resetForm(form: CursoFormGroup, curso: CursoFormGroupInput): void {
    const cursoRawValue = { ...this.getFormDefaults(), ...curso };
    form.reset(
      {
        ...cursoRawValue,
        id: { value: cursoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CursoFormDefaults {
    return {
      id: null,
    };
  }
}
