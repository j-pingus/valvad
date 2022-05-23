import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ModelService } from '../service/model.service';
import { IModel, Model } from '../model.model';
import { ICompatibility } from 'app/entities/compatibility/compatibility.model';
import { CompatibilityService } from 'app/entities/compatibility/service/compatibility.service';

import { ModelUpdateComponent } from './model-update.component';

describe('Model Management Update Component', () => {
  let comp: ModelUpdateComponent;
  let fixture: ComponentFixture<ModelUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let modelService: ModelService;
  let compatibilityService: CompatibilityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ModelUpdateComponent],
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
      .overrideTemplate(ModelUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ModelUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    modelService = TestBed.inject(ModelService);
    compatibilityService = TestBed.inject(CompatibilityService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Compatibility query and add missing value', () => {
      const model: IModel = { id: 456 };
      const compatibility: ICompatibility = { id: 70594 };
      model.compatibility = compatibility;

      const compatibilityCollection: ICompatibility[] = [{ id: 19568 }];
      jest.spyOn(compatibilityService, 'query').mockReturnValue(of(new HttpResponse({ body: compatibilityCollection })));
      const additionalCompatibilities = [compatibility];
      const expectedCollection: ICompatibility[] = [...additionalCompatibilities, ...compatibilityCollection];
      jest.spyOn(compatibilityService, 'addCompatibilityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ model });
      comp.ngOnInit();

      expect(compatibilityService.query).toHaveBeenCalled();
      expect(compatibilityService.addCompatibilityToCollectionIfMissing).toHaveBeenCalledWith(
        compatibilityCollection,
        ...additionalCompatibilities
      );
      expect(comp.compatibilitiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const model: IModel = { id: 456 };
      const compatibility: ICompatibility = { id: 85756 };
      model.compatibility = compatibility;

      activatedRoute.data = of({ model });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(model));
      expect(comp.compatibilitiesSharedCollection).toContain(compatibility);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Model>>();
      const model = { id: 123 };
      jest.spyOn(modelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ model });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: model }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(modelService.update).toHaveBeenCalledWith(model);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Model>>();
      const model = new Model();
      jest.spyOn(modelService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ model });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: model }));
      saveSubject.complete();

      // THEN
      expect(modelService.create).toHaveBeenCalledWith(model);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Model>>();
      const model = { id: 123 };
      jest.spyOn(modelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ model });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(modelService.update).toHaveBeenCalledWith(model);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackCompatibilityById', () => {
      it('Should return tracked Compatibility primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackCompatibilityById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
