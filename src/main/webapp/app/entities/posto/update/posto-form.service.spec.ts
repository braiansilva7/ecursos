import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../posto.test-samples';

import { PostoFormService } from './posto-form.service';

describe('Posto Form Service', () => {
  let service: PostoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PostoFormService);
  });

  describe('Service methods', () => {
    describe('createPostoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPostoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            postoSigla: expect.any(Object),
            descricao: expect.any(Object),
            prioridade: expect.any(Object),
            orgao: expect.any(Object),
            codSigpes: expect.any(Object),
          }),
        );
      });

      it('passing IPosto should create a new form with FormGroup', () => {
        const formGroup = service.createPostoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            postoSigla: expect.any(Object),
            descricao: expect.any(Object),
            prioridade: expect.any(Object),
            orgao: expect.any(Object),
            codSigpes: expect.any(Object),
          }),
        );
      });
    });

    describe('getPosto', () => {
      it('should return NewPosto for default Posto initial value', () => {
        const formGroup = service.createPostoFormGroup(sampleWithNewData);

        const posto = service.getPosto(formGroup) as any;

        expect(posto).toMatchObject(sampleWithNewData);
      });

      it('should return NewPosto for empty Posto initial value', () => {
        const formGroup = service.createPostoFormGroup();

        const posto = service.getPosto(formGroup) as any;

        expect(posto).toMatchObject({});
      });

      it('should return IPosto', () => {
        const formGroup = service.createPostoFormGroup(sampleWithRequiredData);

        const posto = service.getPosto(formGroup) as any;

        expect(posto).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPosto should not enable id FormControl', () => {
        const formGroup = service.createPostoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPosto should disable id FormControl', () => {
        const formGroup = service.createPostoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
