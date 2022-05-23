import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPhoto, Photo } from '../photo.model';

import { PhotoService } from './photo.service';

describe('Photo Service', () => {
  let service: PhotoService;
  let httpMock: HttpTestingController;
  let elemDefault: IPhoto;
  let expectedResult: IPhoto | IPhoto[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PhotoService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      binaryContentType: 'image/png',
      binary: 'AAAAAAA',
      description: 'AAAAAAA',
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

    it('should create a Photo', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Photo()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Photo', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          binary: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Photo', () => {
      const patchObject = Object.assign(
        {
          binary: 'BBBBBB',
        },
        new Photo()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Photo', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          binary: 'BBBBBB',
          description: 'BBBBBB',
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

    it('should delete a Photo', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPhotoToCollectionIfMissing', () => {
      it('should add a Photo to an empty array', () => {
        const photo: IPhoto = { id: 123 };
        expectedResult = service.addPhotoToCollectionIfMissing([], photo);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(photo);
      });

      it('should not add a Photo to an array that contains it', () => {
        const photo: IPhoto = { id: 123 };
        const photoCollection: IPhoto[] = [
          {
            ...photo,
          },
          { id: 456 },
        ];
        expectedResult = service.addPhotoToCollectionIfMissing(photoCollection, photo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Photo to an array that doesn't contain it", () => {
        const photo: IPhoto = { id: 123 };
        const photoCollection: IPhoto[] = [{ id: 456 }];
        expectedResult = service.addPhotoToCollectionIfMissing(photoCollection, photo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(photo);
      });

      it('should add only unique Photo to an array', () => {
        const photoArray: IPhoto[] = [{ id: 123 }, { id: 456 }, { id: 17151 }];
        const photoCollection: IPhoto[] = [{ id: 123 }];
        expectedResult = service.addPhotoToCollectionIfMissing(photoCollection, ...photoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const photo: IPhoto = { id: 123 };
        const photo2: IPhoto = { id: 456 };
        expectedResult = service.addPhotoToCollectionIfMissing([], photo, photo2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(photo);
        expect(expectedResult).toContain(photo2);
      });

      it('should accept null and undefined values', () => {
        const photo: IPhoto = { id: 123 };
        expectedResult = service.addPhotoToCollectionIfMissing([], null, photo, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(photo);
      });

      it('should return initial array if no Photo is added', () => {
        const photoCollection: IPhoto[] = [{ id: 123 }];
        expectedResult = service.addPhotoToCollectionIfMissing(photoCollection, undefined, null);
        expect(expectedResult).toEqual(photoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
