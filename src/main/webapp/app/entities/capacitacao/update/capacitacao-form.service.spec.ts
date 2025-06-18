import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../capacitacao.test-samples';

import { CapacitacaoFormService } from './capacitacao-form.service';

describe('Capacitacao Form Service', () => {
  let service: CapacitacaoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CapacitacaoFormService);
  });

  describe('Service methods', () => {
    describe('createCapacitacaoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCapacitacaoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            capacitacaoStatus: expect.any(Object),
            sigpes: expect.any(Object),
            militar: expect.any(Object),
            turma: expect.any(Object),
          }),
        );
      });

      it('passing ICapacitacao should create a new form with FormGroup', () => {
        const formGroup = service.createCapacitacaoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            capacitacaoStatus: expect.any(Object),
            sigpes: expect.any(Object),
            militar: expect.any(Object),
            turma: expect.any(Object),
          }),
        );
      });
    });

    describe('getCapacitacao', () => {
      it('should return NewCapacitacao for default Capacitacao initial value', () => {
        const formGroup = service.createCapacitacaoFormGroup(sampleWithNewData);

        const capacitacao = service.getCapacitacao(formGroup) as any;

        expect(capacitacao).toMatchObject(sampleWithNewData);
      });

      it('should return NewCapacitacao for empty Capacitacao initial value', () => {
        const formGroup = service.createCapacitacaoFormGroup();

        const capacitacao = service.getCapacitacao(formGroup) as any;

        expect(capacitacao).toMatchObject({});
      });

      it('should return ICapacitacao', () => {
        const formGroup = service.createCapacitacaoFormGroup(sampleWithRequiredData);

        const capacitacao = service.getCapacitacao(formGroup) as any;

        expect(capacitacao).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICapacitacao should not enable id FormControl', () => {
        const formGroup = service.createCapacitacaoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCapacitacao should disable id FormControl', () => {
        const formGroup = service.createCapacitacaoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
