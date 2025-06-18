import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import axios from 'axios';
import { IMilitar, NewMilitar } from '../militar.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMilitar for edit and NewMilitarFormGroupInput for create.
 */
type MilitarFormGroupInput = IMilitar | PartialWithRequiredKeyOf<NewMilitar>;

type MilitarFormDefaults = Pick<NewMilitar, 'id'>;

type MilitarFormGroupContent = {
  id: FormControl<IMilitar['id'] | NewMilitar['id']>;
  saram: FormControl<IMilitar['saram']>;
  nomeCompleto: FormControl<IMilitar['nomeCompleto']>;
  nomeGuerra: FormControl<IMilitar['nomeGuerra']>;
  om: FormControl<IMilitar['om']>;
  telefone: FormControl<IMilitar['telefone']>;
  statusMilitar: FormControl<IMilitar['statusMilitar']>;
  forca: FormControl<IMilitar['forca']>;
  nrAntiguidade: FormControl<IMilitar['nrAntiguidade']>;
  ultimaPromocao: FormControl<IMilitar['ultimaPromocao']>;
  cpf: FormControl<IMilitar['cpf']>;
  email: FormControl<IMilitar['email']>;
  posto: FormControl<IMilitar['posto']>;
};

export type MilitarFormGroup = FormGroup<MilitarFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MilitarFormService {
  createMilitarFormGroup(militar: MilitarFormGroupInput = { id: null }): MilitarFormGroup {
    const militarRawValue = {
      ...this.getFormDefaults(),
      ...militar,
    };
    return new FormGroup<MilitarFormGroupContent>({
      id: new FormControl(
        { value: militarRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      saram: new FormControl(militarRawValue.saram, {
        validators: [Validators.required],
      }),
      nomeCompleto: new FormControl(militarRawValue.nomeCompleto, {
        validators: [Validators.required],
      }),
      nomeGuerra: new FormControl(militarRawValue.nomeGuerra, {
        validators: [Validators.required],
      }),
      om: new FormControl(militarRawValue.om, {
        validators: [Validators.required],
      }),
      telefone: new FormControl(militarRawValue.telefone),
      statusMilitar: new FormControl(militarRawValue.statusMilitar, {
        validators: [Validators.required],
      }),
      forca: new FormControl(militarRawValue.forca, {
        validators: [Validators.required],
      }),
      nrAntiguidade: new FormControl(militarRawValue.nrAntiguidade),
      ultimaPromocao: new FormControl(militarRawValue.ultimaPromocao),
      cpf: new FormControl(militarRawValue.cpf),
      email: new FormControl(militarRawValue.email),
      posto: new FormControl(militarRawValue.posto, {
        validators: [Validators.required],
      }),
    });
  }

  getMilitar(form: MilitarFormGroup): IMilitar | NewMilitar {
    return form.getRawValue() as IMilitar | NewMilitar;
  }

  getMilitarAll(militar: IMilitar | NewMilitar): IMilitar | NewMilitar {
    // Retorna os dados do militar como estão
    return militar;
  }

  resetForm(form: MilitarFormGroup, militar: MilitarFormGroupInput): void {
    const militarRawValue = { ...this.getFormDefaults(), ...militar };
    form.reset(
      {
        ...militarRawValue,
        id: { value: militarRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MilitarFormDefaults {
    return {
      id: null,
    };
  }

}
