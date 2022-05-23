import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AdService } from '../service/ad.service';
import { IAd, Ad } from '../ad.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IPart } from 'app/entities/part/part.model';
import { PartService } from 'app/entities/part/service/part.service';

import { AdUpdateComponent } from './ad-update.component';

describe('Ad Management Update Component', () => {
  let comp: AdUpdateComponent;
  let fixture: ComponentFixture<AdUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let adService: AdService;
  let userService: UserService;
  let partService: PartService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AdUpdateComponent],
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
      .overrideTemplate(AdUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AdUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    adService = TestBed.inject(AdService);
    userService = TestBed.inject(UserService);
    partService = TestBed.inject(PartService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const ad: IAd = { id: 456 };
      const publisher: IUser = { id: 13053 };
      ad.publisher = publisher;

      const userCollection: IUser[] = [{ id: 26507 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [publisher];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ad });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Part query and add missing value', () => {
      const ad: IAd = { id: 456 };
      const part: IPart = { id: 98942 };
      ad.part = part;

      const partCollection: IPart[] = [{ id: 77175 }];
      jest.spyOn(partService, 'query').mockReturnValue(of(new HttpResponse({ body: partCollection })));
      const additionalParts = [part];
      const expectedCollection: IPart[] = [...additionalParts, ...partCollection];
      jest.spyOn(partService, 'addPartToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ad });
      comp.ngOnInit();

      expect(partService.query).toHaveBeenCalled();
      expect(partService.addPartToCollectionIfMissing).toHaveBeenCalledWith(partCollection, ...additionalParts);
      expect(comp.partsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const ad: IAd = { id: 456 };
      const publisher: IUser = { id: 70289 };
      ad.publisher = publisher;
      const part: IPart = { id: 64020 };
      ad.part = part;

      activatedRoute.data = of({ ad });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(ad));
      expect(comp.usersSharedCollection).toContain(publisher);
      expect(comp.partsSharedCollection).toContain(part);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ad>>();
      const ad = { id: 123 };
      jest.spyOn(adService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ad });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ad }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(adService.update).toHaveBeenCalledWith(ad);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ad>>();
      const ad = new Ad();
      jest.spyOn(adService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ad });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ad }));
      saveSubject.complete();

      // THEN
      expect(adService.create).toHaveBeenCalledWith(ad);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ad>>();
      const ad = { id: 123 };
      jest.spyOn(adService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ad });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(adService.update).toHaveBeenCalledWith(ad);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserById', () => {
      it('Should return tracked User primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackPartById', () => {
      it('Should return tracked Part primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPartById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
