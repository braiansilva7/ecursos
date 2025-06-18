import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITipo, NewTipo } from '../tipo.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITipo for edit and NewTipoFormGroupInput for create.
 */
type TipoFormGroupInput = ITipo | PartialWithRequiredKeyOf<NewTipo>;

type TipoFormDefaults = Pick<NewTipo, 'id'>;

type TipoFormGroupContent = {
  id: FormControl<ITipo['id'] | NewTipo['id']>;
  categoria: FormControl<ITipo['categoria']>;
};

export type TipoFormGroup = FormGroup<TipoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TipoFormService {
  createTipoFormGroup(tipo: TipoFormGroupInput = { id: null }): TipoFormGroup {
    const tipoRawValue = {
      ...this.getFormDefaults(),
      ...tipo,
    };
    return new FormGroup<TipoFormGroupContent>({
      id: new FormControl(
        { value: tipoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      categoria: new FormControl(tipoRawValue.categoria, {
        validators: [Validators.required],
      }),
    });
  }

  getTipo(form: TipoFormGroup): ITipo | NewTipo {
    return form.getRawValue() as ITipo | NewTipo;
  }

  resetForm(form: TipoFormGroup, tipo: TipoFormGroupInput): void {
    const tipoRawValue = { ...this.getFormDefaults(), ...tipo };
    form.reset(
      {
        ...tipoRawValue,
        id: { value: tipoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TipoFormDefaults {
    return {
      id: null,
    };
  }
}
