import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPosto, NewPosto } from '../posto.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPosto for edit and NewPostoFormGroupInput for create.
 */
type PostoFormGroupInput = IPosto | PartialWithRequiredKeyOf<NewPosto>;

type PostoFormDefaults = Pick<NewPosto, 'id'>;

type PostoFormGroupContent = {
  id: FormControl<IPosto['id'] | NewPosto['id']>;
  postoSigla: FormControl<IPosto['postoSigla']>;
  descricao: FormControl<IPosto['descricao']>;
  prioridade: FormControl<IPosto['prioridade']>;
  orgao: FormControl<IPosto['orgao']>;
  codSigpes: FormControl<IPosto['codSigpes']>;
};

export type PostoFormGroup = FormGroup<PostoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PostoFormService {
  createPostoFormGroup(posto: PostoFormGroupInput = { id: null }): PostoFormGroup {
    const postoRawValue = {
      ...this.getFormDefaults(),
      ...posto,
    };
    return new FormGroup<PostoFormGroupContent>({
      id: new FormControl(
        { value: postoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      postoSigla: new FormControl(postoRawValue.postoSigla, {
        validators: [Validators.required],
      }),
      descricao: new FormControl(postoRawValue.descricao, {
        validators: [Validators.required],
      }),
      prioridade: new FormControl(postoRawValue.prioridade, {
        validators: [Validators.required],
      }),
      orgao: new FormControl(postoRawValue.orgao, {
        validators: [Validators.required],
      }),
      codSigpes: new FormControl(postoRawValue.codSigpes),
    });
  }

  getPosto(form: PostoFormGroup): IPosto | NewPosto {
    return form.getRawValue() as IPosto | NewPosto;
  }

  resetForm(form: PostoFormGroup, posto: PostoFormGroupInput): void {
    const postoRawValue = { ...this.getFormDefaults(), ...posto };
    form.reset(
      {
        ...postoRawValue,
        id: { value: postoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PostoFormDefaults {
    return {
      id: null,
    };
  }
}
