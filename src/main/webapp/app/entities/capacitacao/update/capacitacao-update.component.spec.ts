import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IMilitar } from 'app/entities/militar/militar.model';
import { MilitarService } from 'app/entities/militar/service/militar.service';
import { ITurma } from 'app/entities/turma/turma.model';
import { TurmaService } from 'app/entities/turma/service/turma.service';
import { ICapacitacao } from '../capacitacao.model';
import { CapacitacaoService } from '../service/capacitacao.service';
import { CapacitacaoFormService } from './capacitacao-form.service';

import { CapacitacaoUpdateComponent } from './capacitacao-update.component';

describe('Capacitacao Management Update Component', () => {
  let comp: CapacitacaoUpdateComponent;
  let fixture: ComponentFixture<CapacitacaoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let capacitacaoFormService: CapacitacaoFormService;
  let capacitacaoService: CapacitacaoService;
  let militarService: MilitarService;
  let turmaService: TurmaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CapacitacaoUpdateComponent],
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
      .overrideTemplate(CapacitacaoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CapacitacaoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    capacitacaoFormService = TestBed.inject(CapacitacaoFormService);
    capacitacaoService = TestBed.inject(CapacitacaoService);
    militarService = TestBed.inject(MilitarService);
    turmaService = TestBed.inject(TurmaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Militar query and add missing value', () => {
      const capacitacao: ICapacitacao = { id: 456 };
      const militar: IMilitar = { id: 18559 };
      capacitacao.militar = militar;

      const militarCollection: IMilitar[] = [{ id: 20432 }];
      jest.spyOn(militarService, 'query').mockReturnValue(of(new HttpResponse({ body: militarCollection })));
      const additionalMilitars = [militar];
      const expectedCollection: IMilitar[] = [...additionalMilitars, ...militarCollection];
      jest.spyOn(militarService, 'addMilitarToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ capacitacao });
      comp.ngOnInit();

      expect(militarService.query).toHaveBeenCalled();
      expect(militarService.addMilitarToCollectionIfMissing).toHaveBeenCalledWith(
        militarCollection,
        ...additionalMilitars.map(expect.objectContaining),
      );
      expect(comp.militarsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Turma query and add missing value', () => {
      const capacitacao: ICapacitacao = { id: 456 };
      const turma: ITurma = { id: 21273 };
      capacitacao.turma = turma;

      const turmaCollection: ITurma[] = [{ id: 12741 }];
      jest.spyOn(turmaService, 'query').mockReturnValue(of(new HttpResponse({ body: turmaCollection })));
      const additionalTurmas = [turma];
      const expectedCollection: ITurma[] = [...additionalTurmas, ...turmaCollection];
      jest.spyOn(turmaService, 'addTurmaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ capacitacao });
      comp.ngOnInit();

      expect(turmaService.query).toHaveBeenCalled();
      expect(turmaService.addTurmaToCollectionIfMissing).toHaveBeenCalledWith(
        turmaCollection,
        ...additionalTurmas.map(expect.objectContaining),
      );
      expect(comp.turmasSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const capacitacao: ICapacitacao = { id: 456 };
      const militar: IMilitar = { id: 19585 };
      capacitacao.militar = militar;
      const turma: ITurma = { id: 26650 };
      capacitacao.turma = turma;

      activatedRoute.data = of({ capacitacao });
      comp.ngOnInit();

      expect(comp.militarsSharedCollection).toContain(militar);
      expect(comp.turmasSharedCollection).toContain(turma);
      expect(comp.capacitacao).toEqual(capacitacao);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICapacitacao>>();
      const capacitacao = { id: 123 };
      jest.spyOn(capacitacaoFormService, 'getCapacitacao').mockReturnValue(capacitacao);
      jest.spyOn(capacitacaoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ capacitacao });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: capacitacao }));
      saveSubject.complete();

      // THEN
      expect(capacitacaoFormService.getCapacitacao).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(capacitacaoService.update).toHaveBeenCalledWith(expect.objectContaining(capacitacao));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICapacitacao>>();
      const capacitacao = { id: 123 };
      jest.spyOn(capacitacaoFormService, 'getCapacitacao').mockReturnValue({ id: null });
      jest.spyOn(capacitacaoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ capacitacao: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: capacitacao }));
      saveSubject.complete();

      // THEN
      expect(capacitacaoFormService.getCapacitacao).toHaveBeenCalled();
      expect(capacitacaoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICapacitacao>>();
      const capacitacao = { id: 123 };
      jest.spyOn(capacitacaoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ capacitacao });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(capacitacaoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareMilitar', () => {
      it('Should forward to militarService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(militarService, 'compareMilitar');
        comp.compareMilitar(entity, entity2);
        expect(militarService.compareMilitar).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTurma', () => {
      it('Should forward to turmaService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(turmaService, 'compareTurma');
        comp.compareTurma(entity, entity2);
        expect(turmaService.compareTurma).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
