import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { TipoService } from '../service/tipo.service';
import { ITipo } from '../tipo.model';
import { TipoFormService } from './tipo-form.service';

import { TipoUpdateComponent } from './tipo-update.component';

describe('Tipo Management Update Component', () => {
  let comp: TipoUpdateComponent;
  let fixture: ComponentFixture<TipoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tipoFormService: TipoFormService;
  let tipoService: TipoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TipoUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TipoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TipoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tipoFormService = TestBed.inject(TipoFormService);
    tipoService = TestBed.inject(TipoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const tipo: ITipo = { id: 456 };

      activatedRoute.data = of({ tipo });
      comp.ngOnInit();

      expect(comp.tipo).toEqual(tipo);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITipo>>();
      const tipo = { id: 123 };
      jest.spyOn(tipoFormService, 'getTipo').mockReturnValue(tipo);
      jest.spyOn(tipoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tipo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tipo }));
      saveSubject.complete();

      // THEN
      expect(tipoFormService.getTipo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tipoService.update).toHaveBeenCalledWith(expect.objectContaining(tipo));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITipo>>();
      const tipo = { id: 123 };
      jest.spyOn(tipoFormService, 'getTipo').mockReturnValue({ id: null });
      jest.spyOn(tipoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tipo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tipo }));
      saveSubject.complete();

      // THEN
      expect(tipoFormService.getTipo).toHaveBeenCalled();
      expect(tipoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITipo>>();
      const tipo = { id: 123 };
      jest.spyOn(tipoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tipo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tipoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
