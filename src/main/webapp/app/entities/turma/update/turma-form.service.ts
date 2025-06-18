import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITurma, NewTurma } from '../turma.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITurma for edit and NewTurmaFormGroupInput for create.
 */
type TurmaFormGroupInput = ITurma | PartialWithRequiredKeyOf<NewTurma>;

type TurmaFormDefaults = Pick<NewTurma, 'id'>;

type TurmaFormGroupContent = {
  id: FormControl<ITurma['id'] | NewTurma['id']>;
  inicio: FormControl<ITurma['inicio']>;
  termino: FormControl<ITurma['termino']>;
  ano: FormControl<ITurma['ano']>;
  statusCurso: FormControl<ITurma['statusCurso']>;
  modalidade: FormControl<ITurma['modalidade']>;
  qtdVagas: FormControl<ITurma['qtdVagas']>;
  numeroBca: FormControl<ITurma['numeroBca']>;
  curso: FormControl<ITurma['curso']>;
};

export type TurmaFormGroup = FormGroup<TurmaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TurmaFormService {
  createTurmaFormGroup(turma: TurmaFormGroupInput = { id: null }): TurmaFormGroup {
    const turmaRawValue = {
      ...this.getFormDefaults(),
      ...turma,
    };
    return new FormGroup<TurmaFormGroupContent>({
      id: new FormControl(
        { value: turmaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      inicio: new FormControl(turmaRawValue.inicio),
      termino: new FormControl(turmaRawValue.termino),
      ano: new FormControl(turmaRawValue.ano),
      statusCurso: new FormControl(turmaRawValue.statusCurso, {
        validators: [Validators.required],
      }),
      modalidade: new FormControl(turmaRawValue.modalidade, {
        validators: [Validators.required],
      }),
      qtdVagas: new FormControl(turmaRawValue.qtdVagas),
      numeroBca: new FormControl(turmaRawValue.numeroBca),
      curso: new FormControl(turmaRawValue.curso, {
        validators: [Validators.required],
      }),
    });
  }

  getTurma(form: TurmaFormGroup): ITurma | NewTurma {
    return form.getRawValue() as ITurma | NewTurma;
  }

  resetForm(form: TurmaFormGroup, turma: TurmaFormGroupInput): void {
    const turmaRawValue = { ...this.getFormDefaults(), ...turma };
    form.reset(
      {
        ...turmaRawValue,
        id: { value: turmaRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TurmaFormDefaults {
    return {
      id: null,
    };
  }
}
