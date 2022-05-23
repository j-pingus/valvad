import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { Subject } from 'app/entities/enumerations/subject.model';
import { Right } from 'app/entities/enumerations/right.model';
import { IPermit, Permit } from '../permit.model';

import { PermitService } from './permit.service';

describe('Permit Service', () => {
  let service: PermitService;
  let httpMock: HttpTestingController;
  let elemDefault: IPermit;
  let expectedResult: IPermit | IPermit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PermitService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      subject: Subject.PART,
      subjectId: 0,
      right: Right.WRITE,
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

    it('should create a Permit', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Permit()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Permit', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          subject: 'BBBBBB',
          subjectId: 1,
          right: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Permit', () => {
      const patchObject = Object.assign(
        {
          subject: 'BBBBBB',
          right: 'BBBBBB',
        },
        new Permit()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Permit', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          subject: 'BBBBBB',
          subjectId: 1,
          right: 'BBBBBB',
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

    it('should delete a Permit', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPermitToCollectionIfMissing', () => {
      it('should add a Permit to an empty array', () => {
        const permit: IPermit = { id: 123 };
        expectedResult = service.addPermitToCollectionIfMissing([], permit);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(permit);
      });

      it('should not add a Permit to an array that contains it', () => {
        const permit: IPermit = { id: 123 };
        const permitCollection: IPermit[] = [
          {
            ...permit,
          },
          { id: 456 },
        ];
        expectedResult = service.addPermitToCollectionIfMissing(permitCollection, permit);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Permit to an array that doesn't contain it", () => {
        const permit: IPermit = { id: 123 };
        const permitCollection: IPermit[] = [{ id: 456 }];
        expectedResult = service.addPermitToCollectionIfMissing(permitCollection, permit);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(permit);
      });

      it('should add only unique Permit to an array', () => {
        const permitArray: IPermit[] = [{ id: 123 }, { id: 456 }, { id: 92526 }];
        const permitCollection: IPermit[] = [{ id: 123 }];
        expectedResult = service.addPermitToCollectionIfMissing(permitCollection, ...permitArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const permit: IPermit = { id: 123 };
        const permit2: IPermit = { id: 456 };
        expectedResult = service.addPermitToCollectionIfMissing([], permit, permit2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(permit);
        expect(expectedResult).toContain(permit2);
      });

      it('should accept null and undefined values', () => {
        const permit: IPermit = { id: 123 };
        expectedResult = service.addPermitToCollectionIfMissing([], null, permit, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(permit);
      });

      it('should return initial array if no Permit is added', () => {
        const permitCollection: IPermit[] = [{ id: 123 }];
        expectedResult = service.addPermitToCollectionIfMissing(permitCollection, undefined, null);
        expect(expectedResult).toEqual(permitCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
