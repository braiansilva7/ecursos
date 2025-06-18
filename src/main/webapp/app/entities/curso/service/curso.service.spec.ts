import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ICurso } from '../curso.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../curso.test-samples';

import { CursoService } from './curso.service';

const requireRestSample: ICurso = {
  ...sampleWithRequiredData,
};

describe('Curso Service', () => {
  let service: CursoService;
  let httpMock: HttpTestingController;
  let expectedResult: ICurso | ICurso[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CursoService);
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

    it('should create a Curso', () => {
      const curso = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(curso).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Curso', () => {
      const curso = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(curso).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Curso', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Curso', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Curso', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Curso', () => {
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

    describe('addCursoToCollectionIfMissing', () => {
      it('should add a Curso to an empty array', () => {
        const curso: ICurso = sampleWithRequiredData;
        expectedResult = service.addCursoToCollectionIfMissing([], curso);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(curso);
      });

      it('should not add a Curso to an array that contains it', () => {
        const curso: ICurso = sampleWithRequiredData;
        const cursoCollection: ICurso[] = [
          {
            ...curso,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCursoToCollectionIfMissing(cursoCollection, curso);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Curso to an array that doesn't contain it", () => {
        const curso: ICurso = sampleWithRequiredData;
        const cursoCollection: ICurso[] = [sampleWithPartialData];
        expectedResult = service.addCursoToCollectionIfMissing(cursoCollection, curso);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(curso);
      });

      it('should add only unique Curso to an array', () => {
        const cursoArray: ICurso[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const cursoCollection: ICurso[] = [sampleWithRequiredData];
        expectedResult = service.addCursoToCollectionIfMissing(cursoCollection, ...cursoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const curso: ICurso = sampleWithRequiredData;
        const curso2: ICurso = sampleWithPartialData;
        expectedResult = service.addCursoToCollectionIfMissing([], curso, curso2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(curso);
        expect(expectedResult).toContain(curso2);
      });

      it('should accept null and undefined values', () => {
        const curso: ICurso = sampleWithRequiredData;
        expectedResult = service.addCursoToCollectionIfMissing([], null, curso, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(curso);
      });

      it('should return initial array if no Curso is added', () => {
        const cursoCollection: ICurso[] = [sampleWithRequiredData];
        expectedResult = service.addCursoToCollectionIfMissing(cursoCollection, undefined, null);
        expect(expectedResult).toEqual(cursoCollection);
      });
    });

    describe('compareCurso', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCurso(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCurso(entity1, entity2);
        const compareResult2 = service.compareCurso(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCurso(entity1, entity2);
        const compareResult2 = service.compareCurso(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCurso(entity1, entity2);
        const compareResult2 = service.compareCurso(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
