import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PartService } from '../service/part.service';
import { IPart, Part } from '../part.model';
import { ICompatibility } from 'app/entities/compatibility/compatibility.model';
import { CompatibilityService } from 'app/entities/compatibility/service/compatibility.service';
import { IBrand } from 'app/entities/brand/brand.model';
import { BrandService } from 'app/entities/brand/service/brand.service';

import { PartUpdateComponent } from './part-update.component';

describe('Part Management Update Component', () => {
  let comp: PartUpdateComponent;
  let fixture: ComponentFixture<PartUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let partService: PartService;
  let compatibilityService: CompatibilityService;
  let brandService: BrandService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PartUpdateComponent],
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
      .overrideTemplate(PartUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PartUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    partService = TestBed.inject(PartService);
    compatibilityService = TestBed.inject(CompatibilityService);
    brandService = TestBed.inject(BrandService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Compatibility query and add missing value', () => {
      const part: IPart = { id: 456 };
      const compatibility: ICompatibility = { id: 89654 };
      part.compatibility = compatibility;

      const compatibilityCollection: ICompatibility[] = [{ id: 25306 }];
      jest.spyOn(compatibilityService, 'query').mockReturnValue(of(new HttpResponse({ body: compatibilityCollection })));
      const additionalCompatibilities = [compatibility];
      const expectedCollection: ICompatibility[] = [...additionalCompatibilities, ...compatibilityCollection];
      jest.spyOn(compatibilityService, 'addCompatibilityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ part });
      comp.ngOnInit();

      expect(compatibilityService.query).toHaveBeenCalled();
      expect(compatibilityService.addCompatibilityToCollectionIfMissing).toHaveBeenCalledWith(
        compatibilityCollection,
        ...additionalCompatibilities
      );
      expect(comp.compatibilitiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Brand query and add missing value', () => {
      const part: IPart = { id: 456 };
      const brand: IBrand = { id: 7340 };
      part.brand = brand;

      const brandCollection: IBrand[] = [{ id: 43357 }];
      jest.spyOn(brandService, 'query').mockReturnValue(of(new HttpResponse({ body: brandCollection })));
      const additionalBrands = [brand];
      const expectedCollection: IBrand[] = [...additionalBrands, ...brandCollection];
      jest.spyOn(brandService, 'addBrandToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ part });
      comp.ngOnInit();

      expect(brandService.query).toHaveBeenCalled();
      expect(brandService.addBrandToCollectionIfMissing).toHaveBeenCalledWith(brandCollection, ...additionalBrands);
      expect(comp.brandsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const part: IPart = { id: 456 };
      const compatibility: ICompatibility = { id: 38885 };
      part.compatibility = compatibility;
      const brand: IBrand = { id: 63590 };
      part.brand = brand;

      activatedRoute.data = of({ part });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(part));
      expect(comp.compatibilitiesSharedCollection).toContain(compatibility);
      expect(comp.brandsSharedCollection).toContain(brand);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Part>>();
      const part = { id: 123 };
      jest.spyOn(partService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ part });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: part }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(partService.update).toHaveBeenCalledWith(part);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Part>>();
      const part = new Part();
      jest.spyOn(partService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ part });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: part }));
      saveSubject.complete();

      // THEN
      expect(partService.create).toHaveBeenCalledWith(part);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Part>>();
      const part = { id: 123 };
      jest.spyOn(partService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ part });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(partService.update).toHaveBeenCalledWith(part);
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

    describe('trackBrandById', () => {
      it('Should return tracked Brand primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackBrandById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
