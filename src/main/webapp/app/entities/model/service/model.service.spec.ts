import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IModel, Model } from '../model.model';

import { ModelService } from './model.service';

describe('Model Service', () => {
  let service: ModelService;
  let httpMock: HttpTestingController;
  let elemDefault: IModel;
  let expectedResult: IModel | IModel[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ModelService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      years: 'AAAAAAA',
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

    it('should create a Model', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Model()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Model', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          years: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Model', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
        },
        new Model()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Model', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          years: 'BBBBBB',
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

    it('should delete a Model', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addModelToCollectionIfMissing', () => {
      it('should add a Model to an empty array', () => {
        const model: IModel = { id: 123 };
        expectedResult = service.addModelToCollectionIfMissing([], model);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(model);
      });

      it('should not add a Model to an array that contains it', () => {
        const model: IModel = { id: 123 };
        const modelCollection: IModel[] = [
          {
            ...model,
          },
          { id: 456 },
        ];
        expectedResult = service.addModelToCollectionIfMissing(modelCollection, model);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Model to an array that doesn't contain it", () => {
        const model: IModel = { id: 123 };
        const modelCollection: IModel[] = [{ id: 456 }];
        expectedResult = service.addModelToCollectionIfMissing(modelCollection, model);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(model);
      });

      it('should add only unique Model to an array', () => {
        const modelArray: IModel[] = [{ id: 123 }, { id: 456 }, { id: 11226 }];
        const modelCollection: IModel[] = [{ id: 123 }];
        expectedResult = service.addModelToCollectionIfMissing(modelCollection, ...modelArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const model: IModel = { id: 123 };
        const model2: IModel = { id: 456 };
        expectedResult = service.addModelToCollectionIfMissing([], model, model2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(model);
        expect(expectedResult).toContain(model2);
      });

      it('should accept null and undefined values', () => {
        const model: IModel = { id: 123 };
        expectedResult = service.addModelToCollectionIfMissing([], null, model, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(model);
      });

      it('should return initial array if no Model is added', () => {
        const modelCollection: IModel[] = [{ id: 123 }];
        expectedResult = service.addModelToCollectionIfMissing(modelCollection, undefined, null);
        expect(expectedResult).toEqual(modelCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
