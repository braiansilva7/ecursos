import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../tipo.test-samples';

import { TipoFormService } from './tipo-form.service';

describe('Tipo Form Service', () => {
  let service: TipoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TipoFormService);
  });

  describe('Service methods', () => {
    describe('createTipoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTipoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            categoria: expect.any(Object),
          }),
        );
      });

      it('passing ITipo should create a new form with FormGroup', () => {
        const formGroup = service.createTipoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            categoria: expect.any(Object),
          }),
        );
      });
    });

    describe('getTipo', () => {
      it('should return NewTipo for default Tipo initial value', () => {
        const formGroup = service.createTipoFormGroup(sampleWithNewData);

        const tipo = service.getTipo(formGroup) as any;

        expect(tipo).toMatchObject(sampleWithNewData);
      });

      it('should return NewTipo for empty Tipo initial value', () => {
        const formGroup = service.createTipoFormGroup();

        const tipo = service.getTipo(formGroup) as any;

        expect(tipo).toMatchObject({});
      });

      it('should return ITipo', () => {
        const formGroup = service.createTipoFormGroup(sampleWithRequiredData);

        const tipo = service.getTipo(formGroup) as any;

        expect(tipo).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITipo should not enable id FormControl', () => {
        const formGroup = service.createTipoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTipo should disable id FormControl', () => {
        const formGroup = service.createTipoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
