import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITipo } from '../tipo.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../tipo.test-samples';

import { TipoService } from './tipo.service';

const requireRestSample: ITipo = {
  ...sampleWithRequiredData,
};

describe('Tipo Service', () => {
  let service: TipoService;
  let httpMock: HttpTestingController;
  let expectedResult: ITipo | ITipo[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TipoService);
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

    it('should create a Tipo', () => {
      const tipo = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(tipo).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Tipo', () => {
      const tipo = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(tipo).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Tipo', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Tipo', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Tipo', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Tipo', () => {
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

    describe('addTipoToCollectionIfMissing', () => {
      it('should add a Tipo to an empty array', () => {
        const tipo: ITipo = sampleWithRequiredData;
        expectedResult = service.addTipoToCollectionIfMissing([], tipo);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tipo);
      });

      it('should not add a Tipo to an array that contains it', () => {
        const tipo: ITipo = sampleWithRequiredData;
        const tipoCollection: ITipo[] = [
          {
            ...tipo,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTipoToCollectionIfMissing(tipoCollection, tipo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Tipo to an array that doesn't contain it", () => {
        const tipo: ITipo = sampleWithRequiredData;
        const tipoCollection: ITipo[] = [sampleWithPartialData];
        expectedResult = service.addTipoToCollectionIfMissing(tipoCollection, tipo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tipo);
      });

      it('should add only unique Tipo to an array', () => {
        const tipoArray: ITipo[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const tipoCollection: ITipo[] = [sampleWithRequiredData];
        expectedResult = service.addTipoToCollectionIfMissing(tipoCollection, ...tipoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tipo: ITipo = sampleWithRequiredData;
        const tipo2: ITipo = sampleWithPartialData;
        expectedResult = service.addTipoToCollectionIfMissing([], tipo, tipo2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tipo);
        expect(expectedResult).toContain(tipo2);
      });

      it('should accept null and undefined values', () => {
        const tipo: ITipo = sampleWithRequiredData;
        expectedResult = service.addTipoToCollectionIfMissing([], null, tipo, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tipo);
      });

      it('should return initial array if no Tipo is added', () => {
        const tipoCollection: ITipo[] = [sampleWithRequiredData];
        expectedResult = service.addTipoToCollectionIfMissing(tipoCollection, undefined, null);
        expect(expectedResult).toEqual(tipoCollection);
      });
    });

    describe('compareTipo', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTipo(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTipo(entity1, entity2);
        const compareResult2 = service.compareTipo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTipo(entity1, entity2);
        const compareResult2 = service.compareTipo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTipo(entity1, entity2);
        const compareResult2 = service.compareTipo(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
