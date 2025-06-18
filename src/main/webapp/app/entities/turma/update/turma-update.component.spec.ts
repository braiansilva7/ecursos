import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ICurso } from 'app/entities/curso/curso.model';
import { CursoService } from 'app/entities/curso/service/curso.service';
import { TurmaService } from '../service/turma.service';
import { ITurma } from '../turma.model';
import { TurmaFormService } from './turma-form.service';

import { TurmaUpdateComponent } from './turma-update.component';

describe('Turma Management Update Component', () => {
  let comp: TurmaUpdateComponent;
  let fixture: ComponentFixture<TurmaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let turmaFormService: TurmaFormService;
  let turmaService: TurmaService;
  let cursoService: CursoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TurmaUpdateComponent],
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
      .overrideTemplate(TurmaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TurmaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    turmaFormService = TestBed.inject(TurmaFormService);
    turmaService = TestBed.inject(TurmaService);
    cursoService = TestBed.inject(CursoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Curso query and add missing value', () => {
      const turma: ITurma = { id: 456 };
      const curso: ICurso = { id: 27023 };
      turma.curso = curso;

      const cursoCollection: ICurso[] = [{ id: 30405 }];
      jest.spyOn(cursoService, 'query').mockReturnValue(of(new HttpResponse({ body: cursoCollection })));
      const additionalCursos = [curso];
      const expectedCollection: ICurso[] = [...additionalCursos, ...cursoCollection];
      jest.spyOn(cursoService, 'addCursoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ turma });
      comp.ngOnInit();

      expect(cursoService.query).toHaveBeenCalled();
      expect(cursoService.addCursoToCollectionIfMissing).toHaveBeenCalledWith(
        cursoCollection,
        ...additionalCursos.map(expect.objectContaining),
      );
      expect(comp.cursosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const turma: ITurma = { id: 456 };
      const curso: ICurso = { id: 22272 };
      turma.curso = curso;

      activatedRoute.data = of({ turma });
      comp.ngOnInit();

      expect(comp.cursosSharedCollection).toContain(curso);
      expect(comp.turma).toEqual(turma);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITurma>>();
      const turma = { id: 123 };
      jest.spyOn(turmaFormService, 'getTurma').mockReturnValue(turma);
      jest.spyOn(turmaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ turma });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: turma }));
      saveSubject.complete();

      // THEN
      expect(turmaFormService.getTurma).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(turmaService.update).toHaveBeenCalledWith(expect.objectContaining(turma));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITurma>>();
      const turma = { id: 123 };
      jest.spyOn(turmaFormService, 'getTurma').mockReturnValue({ id: null });
      jest.spyOn(turmaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ turma: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: turma }));
      saveSubject.complete();

      // THEN
      expect(turmaFormService.getTurma).toHaveBeenCalled();
      expect(turmaService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITurma>>();
      const turma = { id: 123 };
      jest.spyOn(turmaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ turma });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(turmaService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCurso', () => {
      it('Should forward to cursoService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(cursoService, 'compareCurso');
        comp.compareCurso(entity, entity2);
        expect(cursoService.compareCurso).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
