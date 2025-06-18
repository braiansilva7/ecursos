import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICapacitacao, NewCapacitacao } from '../capacitacao.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICapacitacao for edit and NewCapacitacaoFormGroupInput for create.
 */
type CapacitacaoFormGroupInput = ICapacitacao | PartialWithRequiredKeyOf<NewCapacitacao>;

type CapacitacaoFormDefaults = Pick<NewCapacitacao, 'id'>;

type CapacitacaoFormGroupContent = {
  id: FormControl<ICapacitacao['id'] | NewCapacitacao['id']>;
  capacitacaoStatus: FormControl<ICapacitacao['capacitacaoStatus']>;
  sigpes: FormControl<ICapacitacao['sigpes']>;
  militar: FormControl<ICapacitacao['militar']>;
  turma: FormControl<ICapacitacao['turma']>;
};

export type CapacitacaoFormGroup = FormGroup<CapacitacaoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CapacitacaoFormService {
  createCapacitacaoFormGroup(capacitacao: CapacitacaoFormGroupInput = { id: null }): CapacitacaoFormGroup {
    const capacitacaoRawValue = {
      ...this.getFormDefaults(),
      ...capacitacao,
    };
    return new FormGroup<CapacitacaoFormGroupContent>({
      id: new FormControl(
        { value: capacitacaoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      capacitacaoStatus: new FormControl(capacitacaoRawValue.capacitacaoStatus, {
        validators: [Validators.required],
      }),
      sigpes: new FormControl(capacitacaoRawValue.sigpes),
      militar: new FormControl(capacitacaoRawValue.militar),
      turma: new FormControl(capacitacaoRawValue.turma, {
        validators: [Validators.required],
      }),
    });
  }

  getCapacitacao(form: CapacitacaoFormGroup): ICapacitacao | NewCapacitacao {
    return form.getRawValue() as ICapacitacao | NewCapacitacao;
  }

  resetForm(form: CapacitacaoFormGroup, capacitacao: CapacitacaoFormGroupInput): void {
    const capacitacaoRawValue = { ...this.getFormDefaults(), ...capacitacao };
    form.reset(
      {
        ...capacitacaoRawValue,
        id: { value: capacitacaoRawValue.id, disabled: true },
      } as any
    );
  }

  private getFormDefaults(): CapacitacaoFormDefaults {
    return {
      id: null,
    };
  }
}
