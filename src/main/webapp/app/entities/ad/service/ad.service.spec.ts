import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { Quality } from 'app/entities/enumerations/quality.model';
import { IAd, Ad } from '../ad.model';

import { AdService } from './ad.service';

describe('Ad Service', () => {
  let service: AdService;
  let httpMock: HttpTestingController;
  let elemDefault: IAd;
  let expectedResult: IAd | IAd[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AdService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      description: 'AAAAAAA',
      quality: Quality.UNKNOWN,
      price: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Ad', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Ad()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Ad', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          description: 'BBBBBB',
          quality: 'BBBBBB',
          price: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Ad', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
          price: 1,
        },
        new Ad()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Ad', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          description: 'BBBBBB',
          quality: 'BBBBBB',
          price: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Ad', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAdToCollectionIfMissing', () => {
      it('should add a Ad to an empty array', () => {
        const ad: IAd = { id: 123 };
        expectedResult = service.addAdToCollectionIfMissing([], ad);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ad);
      });

      it('should not add a Ad to an array that contains it', () => {
        const ad: IAd = { id: 123 };
        const adCollection: IAd[] = [
          {
            ...ad,
          },
          { id: 456 },
        ];
        expectedResult = service.addAdToCollectionIfMissing(adCollection, ad);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Ad to an array that doesn't contain it", () => {
        const ad: IAd = { id: 123 };
        const adCollection: IAd[] = [{ id: 456 }];
        expectedResult = service.addAdToCollectionIfMissing(adCollection, ad);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ad);
      });

      it('should add only unique Ad to an array', () => {
        const adArray: IAd[] = [{ id: 123 }, { id: 456 }, { id: 29144 }];
        const adCollection: IAd[] = [{ id: 123 }];
        expectedResult = service.addAdToCollectionIfMissing(adCollection, ...adArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ad: IAd = { id: 123 };
        const ad2: IAd = { id: 456 };
        expectedResult = service.addAdToCollectionIfMissing([], ad, ad2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ad);
        expect(expectedResult).toContain(ad2);
      });

      it('should accept null and undefined values', () => {
        const ad: IAd = { id: 123 };
        expectedResult = service.addAdToCollectionIfMissing([], null, ad, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ad);
      });

      it('should return initial array if no Ad is added', () => {
        const adCollection: IAd[] = [{ id: 123 }];
        expectedResult = service.addAdToCollectionIfMissing(adCollection, undefined, null);
        expect(expectedResult).toEqual(adCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
