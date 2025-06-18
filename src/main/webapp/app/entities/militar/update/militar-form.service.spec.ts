import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../militar.test-samples';

import { MilitarFormService } from './militar-form.service';

describe('Militar Form Service', () => {
  let service: MilitarFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MilitarFormService);
  });

  describe('Service methods', () => {
    describe('createMilitarFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMilitarFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            saram: expect.any(Object),
            nomeCompleto: expect.any(Object),
            nomeGuerra: expect.any(Object),
            om: expect.any(Object),
            telefone: expect.any(Object),
            statusMilitar: expect.any(Object),
            forca: expect.any(Object),
            nrAntiguidade: expect.any(Object),
            ultimaPromocao: expect.any(Object),
            cpf: expect.any(Object),
            email: expect.any(Object),
            posto: expect.any(Object),
          }),
        );
      });

      it('passing IMilitar should create a new form with FormGroup', () => {
        const formGroup = service.createMilitarFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            saram: expect.any(Object),
            nomeCompleto: expect.any(Object),
            nomeGuerra: expect.any(Object),
            om: expect.any(Object),
            telefone: expect.any(Object),
            statusMilitar: expect.any(Object),
            forca: expect.any(Object),
            nrAntiguidade: expect.any(Object),
            ultimaPromocao: expect.any(Object),
            cpf: expect.any(Object),
            email: expect.any(Object),
            posto: expect.any(Object),
          }),
        );
      });
    });

    describe('getMilitar', () => {
      it('should return NewMilitar for default Militar initial value', () => {
        const formGroup = service.createMilitarFormGroup(sampleWithNewData);

        const militar = service.getMilitar(formGroup) as any;

        expect(militar).toMatchObject(sampleWithNewData);
      });

      it('should return NewMilitar for empty Militar initial value', () => {
        const formGroup = service.createMilitarFormGroup();

        const militar = service.getMilitar(formGroup) as any;

        expect(militar).toMatchObject({});
      });

      it('should return IMilitar', () => {
        const formGroup = service.createMilitarFormGroup(sampleWithRequiredData);

        const militar = service.getMilitar(formGroup) as any;

        expect(militar).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMilitar should not enable id FormControl', () => {
        const formGroup = service.createMilitarFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMilitar should disable id FormControl', () => {
        const formGroup = service.createMilitarFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
