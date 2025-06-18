import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPosto } from 'app/entities/posto/posto.model';
import { PostoService } from 'app/entities/posto/service/posto.service';
import { MilitarService } from '../service/militar.service';
import { IMilitar } from '../militar.model';
import { MilitarFormService } from './militar-form.service';

import { MilitarUpdateComponent } from './militar-update.component';

describe('Militar Management Update Component', () => {
  let comp: MilitarUpdateComponent;
  let fixture: ComponentFixture<MilitarUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let militarFormService: MilitarFormService;
  let militarService: MilitarService;
  let postoService: PostoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MilitarUpdateComponent],
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
      .overrideTemplate(MilitarUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MilitarUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    militarFormService = TestBed.inject(MilitarFormService);
    militarService = TestBed.inject(MilitarService);
    postoService = TestBed.inject(PostoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Posto query and add missing value', () => {
      const militar: IMilitar = { id: 456 };
      const posto: IPosto = { id: 19943 };
      militar.posto = posto;

      const postoCollection: IPosto[] = [{ id: 5462 }];
      jest.spyOn(postoService, 'query').mockReturnValue(of(new HttpResponse({ body: postoCollection })));
      const additionalPostos = [posto];
      const expectedCollection: IPosto[] = [...additionalPostos, ...postoCollection];
      jest.spyOn(postoService, 'addPostoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ militar });
      comp.ngOnInit();

      expect(postoService.query).toHaveBeenCalled();
      expect(postoService.addPostoToCollectionIfMissing).toHaveBeenCalledWith(
        postoCollection,
        ...additionalPostos.map(expect.objectContaining),
      );
      expect(comp.postosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const militar: IMilitar = { id: 456 };
      const posto: IPosto = { id: 2456 };
      militar.posto = posto;

      activatedRoute.data = of({ militar });
      comp.ngOnInit();

      expect(comp.postosSharedCollection).toContain(posto);
      expect(comp.militar).toEqual(militar);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMilitar>>();
      const militar = { id: 123 };
      jest.spyOn(militarFormService, 'getMilitar').mockReturnValue(militar);
      jest.spyOn(militarService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ militar });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: militar }));
      saveSubject.complete();

      // THEN
      expect(militarFormService.getMilitar).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(militarService.update).toHaveBeenCalledWith(expect.objectContaining(militar));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMilitar>>();
      const militar = { id: 123 };
      jest.spyOn(militarFormService, 'getMilitar').mockReturnValue({ id: null });
      jest.spyOn(militarService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ militar: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: militar }));
      saveSubject.complete();

      // THEN
      expect(militarFormService.getMilitar).toHaveBeenCalled();
      expect(militarService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMilitar>>();
      const militar = { id: 123 };
      jest.spyOn(militarService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ militar });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(militarService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePosto', () => {
      it('Should forward to postoService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(postoService, 'comparePosto');
        comp.comparePosto(entity, entity2);
        expect(postoService.comparePosto).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
