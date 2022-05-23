import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CompatibilityService } from '../service/compatibility.service';
import { ICompatibility, Compatibility } from '../compatibility.model';
import { IPart } from 'app/entities/part/part.model';
import { PartService } from 'app/entities/part/service/part.service';
import { IModel } from 'app/entities/model/model.model';
import { ModelService } from 'app/entities/model/service/model.service';

import { CompatibilityUpdateComponent } from './compatibility-update.component';

describe('Compatibility Management Update Component', () => {
  let comp: CompatibilityUpdateComponent;
  let fixture: ComponentFixture<CompatibilityUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let compatibilityService: CompatibilityService;
  let partService: PartService;
  let modelService: ModelService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CompatibilityUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CompatibilityUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CompatibilityUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    compatibilityService = TestBed.inject(CompatibilityService);
    partService = TestBed.inject(PartService);
    modelService = TestBed.inject(ModelService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Part query and add missing value', () => {
      const compatibility: ICompatibility = { id: 456 };
      const part: IPart = { id: 73711 };
      compatibility.part = part;

      const partCollection: IPart[] = [{ id: 14609 }];
      jest.spyOn(partService, 'query').mockReturnValue(of(new HttpResponse({ body: partCollection })));
      const additionalParts = [part];
      const expectedCollection: IPart[] = [...additionalParts, ...partCollection];
      jest.spyOn(partService, 'addPartToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ compatibility });
      comp.ngOnInit();

      expect(partService.query).toHaveBeenCalled();
      expect(partService.addPartToCollectionIfMissing).toHaveBeenCalledWith(partCollection, ...additionalParts);
      expect(comp.partsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Model query and add missing value', () => {
      const compatibility: ICompatibility = { id: 456 };
      const model: IModel = { id: 58246 };
      compatibility.model = model;

      const modelCollection: IModel[] = [{ id: 82309 }];
      jest.spyOn(modelService, 'query').mockReturnValue(of(new HttpResponse({ body: modelCollection })));
      const additionalModels = [model];
      const expectedCollection: IModel[] = [...additionalModels, ...modelCollection];
      jest.spyOn(modelService, 'addModelToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ compatibility });
      comp.ngOnInit();

      expect(modelService.query).toHaveBeenCalled();
      expect(modelService.addModelToCollectionIfMissing).toHaveBeenCalledWith(modelCollection, ...additionalModels);
      expect(comp.modelsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const compatibility: ICompatibility = { id: 456 };
      const part: IPart = { id: 37366 };
      compatibility.part = part;
      const model: IModel = { id: 4927 };
      compatibility.model = model;

      activatedRoute.data = of({ compatibility });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(compatibility));
      expect(comp.partsSharedCollection).toContain(part);
      expect(comp.modelsSharedCollection).toContain(model);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Compatibility>>();
      const compatibility = { id: 123 };
      jest.spyOn(compatibilityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ compatibility });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: compatibility }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(compatibilityService.update).toHaveBeenCalledWith(compatibility);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Compatibility>>();
      const compatibility = new Compatibility();
      jest.spyOn(compatibilityService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ compatibility });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: compatibility }));
      saveSubject.complete();

      // THEN
      expect(compatibilityService.create).toHaveBeenCalledWith(compatibility);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Compatibility>>();
      const compatibility = { id: 123 };
      jest.spyOn(compatibilityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ compatibility });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(compatibilityService.update).toHaveBeenCalledWith(compatibility);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackPartById', () => {
      it('Should return tracked Part primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPartById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackModelById', () => {
      it('Should return tracked Model primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackModelById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
