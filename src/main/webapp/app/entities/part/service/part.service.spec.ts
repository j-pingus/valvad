import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPart, Part } from '../part.model';

import { PartService } from './part.service';

describe('Part Service', () => {
  let service: PartService;
  let httpMock: HttpTestingController;
  let elemDefault: IPart;
  let expectedResult: IPart | IPart[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PartService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      description: 'AAAAAAA',
      number: 'AAAAAAA',
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

    it('should create a Part', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Part()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Part', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          description: 'BBBBBB',
          number: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Part', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
          number: 'BBBBBB',
        },
        new Part()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Part', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          description: 'BBBBBB',
          number: 'BBBBBB',
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

    it('should delete a Part', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPartToCollectionIfMissing', () => {
      it('should add a Part to an empty array', () => {
        const part: IPart = { id: 123 };
        expectedResult = service.addPartToCollectionIfMissing([], part);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(part);
      });

      it('should not add a Part to an array that contains it', () => {
        const part: IPart = { id: 123 };
        const partCollection: IPart[] = [
          {
            ...part,
          },
          { id: 456 },
        ];
        expectedResult = service.addPartToCollectionIfMissing(partCollection, part);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Part to an array that doesn't contain it", () => {
        const part: IPart = { id: 123 };
        const partCollection: IPart[] = [{ id: 456 }];
        expectedResult = service.addPartToCollectionIfMissing(partCollection, part);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(part);
      });

      it('should add only unique Part to an array', () => {
        const partArray: IPart[] = [{ id: 123 }, { id: 456 }, { id: 53000 }];
        const partCollection: IPart[] = [{ id: 123 }];
        expectedResult = service.addPartToCollectionIfMissing(partCollection, ...partArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const part: IPart = { id: 123 };
        const part2: IPart = { id: 456 };
        expectedResult = service.addPartToCollectionIfMissing([], part, part2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(part);
        expect(expectedResult).toContain(part2);
      });

      it('should accept null and undefined values', () => {
        const part: IPart = { id: 123 };
        expectedResult = service.addPartToCollectionIfMissing([], null, part, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(part);
      });

      it('should return initial array if no Part is added', () => {
        const partCollection: IPart[] = [{ id: 123 }];
        expectedResult = service.addPartToCollectionIfMissing(partCollection, undefined, null);
        expect(expectedResult).toEqual(partCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
