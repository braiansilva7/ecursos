import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITipo } from 'app/entities/tipo/tipo.model';
import { TipoService } from 'app/entities/tipo/service/tipo.service';
import { CursoService } from '../service/curso.service';
import { ICurso } from '../curso.model';
import { CursoFormService } from './curso-form.service';

import { CursoUpdateComponent } from './curso-update.component';

describe('Curso Management Update Component', () => {
  let comp: CursoUpdateComponent;
  let fixture: ComponentFixture<CursoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cursoFormService: CursoFormService;
  let cursoService: CursoService;
  let tipoService: TipoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CursoUpdateComponent],
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
      .overrideTemplate(CursoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CursoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cursoFormService = TestBed.inject(CursoFormService);
    cursoService = TestBed.inject(CursoService);
    tipoService = TestBed.inject(TipoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Tipo query and add missing value', () => {
      const curso: ICurso = { id: 456 };
      const tipo: ITipo = { id: 2908 };
      curso.tipo = tipo;

      const tipoCollection: ITipo[] = [{ id: 3242 }];
      jest.spyOn(tipoService, 'query').mockReturnValue(of(new HttpResponse({ body: tipoCollection })));
      const additionalTipos = [tipo];
      const expectedCollection: ITipo[] = [...additionalTipos, ...tipoCollection];
      jest.spyOn(tipoService, 'addTipoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ curso });
      comp.ngOnInit();

      expect(tipoService.query).toHaveBeenCalled();
      expect(tipoService.addTipoToCollectionIfMissing).toHaveBeenCalledWith(
        tipoCollection,
        ...additionalTipos.map(expect.objectContaining),
      );
      expect(comp.tiposSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const curso: ICurso = { id: 456 };
      const tipo: ITipo = { id: 11950 };
      curso.tipo = tipo;

      activatedRoute.data = of({ curso });
      comp.ngOnInit();

      expect(comp.tiposSharedCollection).toContain(tipo);
      expect(comp.curso).toEqual(curso);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICurso>>();
      const curso = { id: 123 };
      jest.spyOn(cursoFormService, 'getCurso').mockReturnValue(curso);
      jest.spyOn(cursoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ curso });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: curso }));
      saveSubject.complete();

      // THEN
      expect(cursoFormService.getCurso).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cursoService.update).toHaveBeenCalledWith(expect.objectContaining(curso));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICurso>>();
      const curso = { id: 123 };
      jest.spyOn(cursoFormService, 'getCurso').mockReturnValue({ id: null });
      jest.spyOn(cursoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ curso: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: curso }));
      saveSubject.complete();

      // THEN
      expect(cursoFormService.getCurso).toHaveBeenCalled();
      expect(cursoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICurso>>();
      const curso = { id: 123 };
      jest.spyOn(cursoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ curso });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cursoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTipo', () => {
      it('Should forward to tipoService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(tipoService, 'compareTipo');
        comp.compareTipo(entity, entity2);
        expect(tipoService.compareTipo).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
