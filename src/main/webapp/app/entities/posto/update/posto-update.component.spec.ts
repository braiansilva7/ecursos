import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { PostoService } from '../service/posto.service';
import { IPosto } from '../posto.model';
import { PostoFormService } from './posto-form.service';

import { PostoUpdateComponent } from './posto-update.component';

describe('Posto Management Update Component', () => {
  let comp: PostoUpdateComponent;
  let fixture: ComponentFixture<PostoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let postoFormService: PostoFormService;
  let postoService: PostoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PostoUpdateComponent],
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
      .overrideTemplate(PostoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PostoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    postoFormService = TestBed.inject(PostoFormService);
    postoService = TestBed.inject(PostoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const posto: IPosto = { id: 456 };

      activatedRoute.data = of({ posto });
      comp.ngOnInit();

      expect(comp.posto).toEqual(posto);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPosto>>();
      const posto = { id: 123 };
      jest.spyOn(postoFormService, 'getPosto').mockReturnValue(posto);
      jest.spyOn(postoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ posto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: posto }));
      saveSubject.complete();

      // THEN
      expect(postoFormService.getPosto).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(postoService.update).toHaveBeenCalledWith(expect.objectContaining(posto));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPosto>>();
      const posto = { id: 123 };
      jest.spyOn(postoFormService, 'getPosto').mockReturnValue({ id: null });
      jest.spyOn(postoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ posto: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: posto }));
      saveSubject.complete();

      // THEN
      expect(postoFormService.getPosto).toHaveBeenCalled();
      expect(postoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPosto>>();
      const posto = { id: 123 };
      jest.spyOn(postoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ posto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(postoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
