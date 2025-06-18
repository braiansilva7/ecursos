import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ICurso, NewCurso } from '../curso.model';

export type PartialUpdateCurso = Partial<ICurso> & Pick<ICurso, 'id'>;

export type EntityResponseType = HttpResponse<ICurso>;
export type EntityArrayResponseType = HttpResponse<ICurso[]>;

@Injectable({ providedIn: 'root' })
export class CursoService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cursos');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/cursos/_search');

  create(curso: NewCurso): Observable<EntityResponseType> {
    return this.http.post<ICurso>(this.resourceUrl, curso, { observe: 'response' });
  }

  update(curso: ICurso): Observable<EntityResponseType> {
    return this.http.put<ICurso>(`${this.resourceUrl}/${this.getCursoIdentifier(curso)}`, curso, { observe: 'response' });
  }

  partialUpdate(curso: PartialUpdateCurso): Observable<EntityResponseType> {
    return this.http.patch<ICurso>(`${this.resourceUrl}/${this.getCursoIdentifier(curso)}`, curso, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICurso>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICurso[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  queryAll(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICurso[]>(`${this.resourceUrl}/all`, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICurso[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ICurso[]>()], asapScheduler)));
  }

  getCursoIdentifier(curso: Pick<ICurso, 'id'>): number {
    return curso.id;
  }

  compareCurso(o1: Pick<ICurso, 'id'> | null, o2: Pick<ICurso, 'id'> | null): boolean {
    return o1 && o2 ? this.getCursoIdentifier(o1) === this.getCursoIdentifier(o2) : o1 === o2;
  }

  addCursoToCollectionIfMissing<Type extends Pick<ICurso, 'id'>>(
    cursoCollection: Type[],
    ...cursosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cursos: Type[] = cursosToCheck.filter(isPresent);
    if (cursos.length > 0) {
      const cursoCollectionIdentifiers = cursoCollection.map(cursoItem => this.getCursoIdentifier(cursoItem));
      const cursosToAdd = cursos.filter(cursoItem => {
        const cursoIdentifier = this.getCursoIdentifier(cursoItem);
        if (cursoCollectionIdentifiers.includes(cursoIdentifier)) {
          return false;
        }
        cursoCollectionIdentifiers.push(cursoIdentifier);
        return true;
      });
      return [...cursosToAdd, ...cursoCollection];
    }
    return cursoCollection;
  }
}
