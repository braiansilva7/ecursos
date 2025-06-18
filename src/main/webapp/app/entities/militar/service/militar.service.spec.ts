import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IMilitar } from '../militar.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../militar.test-samples';

import { MilitarService, RestMilitar } from './militar.service';

const requireRestSample: RestMilitar = {
  ...sampleWithRequiredData,
  ultimaPromocao: sampleWithRequiredData.ultimaPromocao?.format(DATE_FORMAT),
};

describe('Militar Service', () => {
  let service: MilitarService;
  let httpMock: HttpTestingController;
  let expectedResult: IMilitar | IMilitar[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MilitarService);
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

    it('should create a Militar', () => {
      const militar = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(militar).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Militar', () => {
      const militar = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(militar).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Militar', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Militar', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Militar', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Militar', () => {
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

    describe('addMilitarToCollectionIfMissing', () => {
      it('should add a Militar to an empty array', () => {
        const militar: IMilitar = sampleWithRequiredData;
        expectedResult = service.addMilitarToCollectionIfMissing([], militar);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(militar);
      });

      it('should not add a Militar to an array that contains it', () => {
        const militar: IMilitar = sampleWithRequiredData;
        const militarCollection: IMilitar[] = [
          {
            ...militar,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMilitarToCollectionIfMissing(militarCollection, militar);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Militar to an array that doesn't contain it", () => {
        const militar: IMilitar = sampleWithRequiredData;
        const militarCollection: IMilitar[] = [sampleWithPartialData];
        expectedResult = service.addMilitarToCollectionIfMissing(militarCollection, militar);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(militar);
      });

      it('should add only unique Militar to an array', () => {
        const militarArray: IMilitar[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const militarCollection: IMilitar[] = [sampleWithRequiredData];
        expectedResult = service.addMilitarToCollectionIfMissing(militarCollection, ...militarArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const militar: IMilitar = sampleWithRequiredData;
        const militar2: IMilitar = sampleWithPartialData;
        expectedResult = service.addMilitarToCollectionIfMissing([], militar, militar2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(militar);
        expect(expectedResult).toContain(militar2);
      });

      it('should accept null and undefined values', () => {
        const militar: IMilitar = sampleWithRequiredData;
        expectedResult = service.addMilitarToCollectionIfMissing([], null, militar, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(militar);
      });

      it('should return initial array if no Militar is added', () => {
        const militarCollection: IMilitar[] = [sampleWithRequiredData];
        expectedResult = service.addMilitarToCollectionIfMissing(militarCollection, undefined, null);
        expect(expectedResult).toEqual(militarCollection);
      });
    });

    describe('compareMilitar', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMilitar(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMilitar(entity1, entity2);
        const compareResult2 = service.compareMilitar(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareMilitar(entity1, entity2);
        const compareResult2 = service.compareMilitar(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareMilitar(entity1, entity2);
        const compareResult2 = service.compareMilitar(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
