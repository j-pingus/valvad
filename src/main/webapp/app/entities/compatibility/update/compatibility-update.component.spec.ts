import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CompatibilityService } from '../service/compatibility.service';
import { ICompatibility, Compatibility } from '../compatibility.model';

import { CompatibilityUpdateComponent } from './compatibility-update.component';

describe('Compatibility Management Update Component', () => {
  let comp: CompatibilityUpdateComponent;
  let fixture: ComponentFixture<CompatibilityUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let compatibilityService: CompatibilityService;

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

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const compatibility: ICompatibility = { id: 456 };

      activatedRoute.data = of({ compatibility });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(compatibility));
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
});
