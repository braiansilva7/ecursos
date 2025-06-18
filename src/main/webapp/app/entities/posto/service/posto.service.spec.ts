import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IPosto } from '../posto.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../posto.test-samples';

import { PostoService } from './posto.service';

const requireRestSample: IPosto = {
  ...sampleWithRequiredData,
};

describe('Posto Service', () => {
  let service: PostoService;
  let httpMock: HttpTestingController;
  let expectedResult: IPosto | IPosto[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PostoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Posto', () => {
      const posto = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(posto).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Posto', () => {
      const posto = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(posto).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Posto', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Posto', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Posto', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Posto', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addPostoToCollectionIfMissing', () => {
      it('should add a Posto to an empty array', () => {
        const posto: IPosto = sampleWithRequiredData;
        expectedResult = service.addPostoToCollectionIfMissing([], posto);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(posto);
      });

      it('should not add a Posto to an array that contains it', () => {
        const posto: IPosto = sampleWithRequiredData;
        const postoCollection: IPosto[] = [
          {
            ...posto,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPostoToCollectionIfMissing(postoCollection, posto);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Posto to an array that doesn't contain it", () => {
        const posto: IPosto = sampleWithRequiredData;
        const postoCollection: IPosto[] = [sampleWithPartialData];
        expectedResult = service.addPostoToCollectionIfMissing(postoCollection, posto);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(posto);
      });

      it('should add only unique Posto to an array', () => {
        const postoArray: IPosto[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const postoCollection: IPosto[] = [sampleWithRequiredData];
        expectedResult = service.addPostoToCollectionIfMissing(postoCollection, ...postoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const posto: IPosto = sampleWithRequiredData;
        const posto2: IPosto = sampleWithPartialData;
        expectedResult = service.addPostoToCollectionIfMissing([], posto, posto2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(posto);
        expect(expectedResult).toContain(posto2);
      });

      it('should accept null and undefined values', () => {
        const posto: IPosto = sampleWithRequiredData;
        expectedResult = service.addPostoToCollectionIfMissing([], null, posto, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(posto);
      });

      it('should return initial array if no Posto is added', () => {
        const postoCollection: IPosto[] = [sampleWithRequiredData];
        expectedResult = service.addPostoToCollectionIfMissing(postoCollection, undefined, null);
        expect(expectedResult).toEqual(postoCollection);
      });
    });

    describe('comparePosto', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePosto(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePosto(entity1, entity2);
        const compareResult2 = service.comparePosto(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePosto(entity1, entity2);
        const compareResult2 = service.comparePosto(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePosto(entity1, entity2);
        const compareResult2 = service.comparePosto(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
