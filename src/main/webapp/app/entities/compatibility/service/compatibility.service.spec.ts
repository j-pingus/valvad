import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICompatibility, Compatibility } from '../compatibility.model';

import { CompatibilityService } from './compatibility.service';

describe('Compatibility Service', () => {
  let service: CompatibilityService;
  let httpMock: HttpTestingController;
  let elemDefault: ICompatibility;
  let expectedResult: ICompatibility | ICompatibility[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CompatibilityService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
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

    it('should create a Compatibility', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Compatibility()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Compatibility', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Compatibility', () => {
      const patchObject = Object.assign({}, new Compatibility());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Compatibility', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
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

    it('should delete a Compatibility', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCompatibilityToCollectionIfMissing', () => {
      it('should add a Compatibility to an empty array', () => {
        const compatibility: ICompatibility = { id: 123 };
        expectedResult = service.addCompatibilityToCollectionIfMissing([], compatibility);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(compatibility);
      });

      it('should not add a Compatibility to an array that contains it', () => {
        const compatibility: ICompatibility = { id: 123 };
        const compatibilityCollection: ICompatibility[] = [
          {
            ...compatibility,
          },
          { id: 456 },
        ];
        expectedResult = service.addCompatibilityToCollectionIfMissing(compatibilityCollection, compatibility);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Compatibility to an array that doesn't contain it", () => {
        const compatibility: ICompatibility = { id: 123 };
        const compatibilityCollection: ICompatibility[] = [{ id: 456 }];
        expectedResult = service.addCompatibilityToCollectionIfMissing(compatibilityCollection, compatibility);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(compatibility);
      });

      it('should add only unique Compatibility to an array', () => {
        const compatibilityArray: ICompatibility[] = [{ id: 123 }, { id: 456 }, { id: 46078 }];
        const compatibilityCollection: ICompatibility[] = [{ id: 123 }];
        expectedResult = service.addCompatibilityToCollectionIfMissing(compatibilityCollection, ...compatibilityArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const compatibility: ICompatibility = { id: 123 };
        const compatibility2: ICompatibility = { id: 456 };
        expectedResult = service.addCompatibilityToCollectionIfMissing([], compatibility, compatibility2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(compatibility);
        expect(expectedResult).toContain(compatibility2);
      });

      it('should accept null and undefined values', () => {
        const compatibility: ICompatibility = { id: 123 };
        expectedResult = service.addCompatibilityToCollectionIfMissing([], null, compatibility, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(compatibility);
      });

      it('should return initial array if no Compatibility is added', () => {
        const compatibilityCollection: ICompatibility[] = [{ id: 123 }];
        expectedResult = service.addCompatibilityToCollectionIfMissing(compatibilityCollection, undefined, null);
        expect(expectedResult).toEqual(compatibilityCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
