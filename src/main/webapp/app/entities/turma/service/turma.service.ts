import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITurma, NewTurma } from '../turma.model';

export type PartialUpdateTurma = Partial<ITurma> & Pick<ITurma, 'id'>;

type RestOf<T extends ITurma | NewTurma> = Omit<T, 'inicio' | 'termino'> & {
  inicio?: string | null;
  termino?: string | null;
};

export type RestTurma = RestOf<ITurma>;

export type NewRestTurma = RestOf<NewTurma>;

export type PartialUpdateRestTurma = RestOf<PartialUpdateTurma>;

export type EntityResponseType = HttpResponse<ITurma>;
export type EntityArrayResponseType = HttpResponse<ITurma[]>;

@Injectable({ providedIn: 'root' })
export class TurmaService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/turmas');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/turmas/_search');

  create(turma: NewTurma): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(turma);
    return this.http.post<RestTurma>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(turma: ITurma): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(turma);
    return this.http
      .put<RestTurma>(`${this.resourceUrl}/${this.getTurmaIdentifier(turma)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(turma: PartialUpdateTurma): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(turma);
    return this.http
      .patch<RestTurma>(`${this.resourceUrl}/${this.getTurmaIdentifier(turma)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTurma>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITurma[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  queryAll(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITurma[]>(`${this.resourceUrl}/all`, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  countAvailableSeats(id: number): Observable<number> {
    return this.http.get<number>(`${this.resourceUrl}/${id}/vagas-disponiveis`);
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestTurma[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),

      catchError(() => scheduled([new HttpResponse<ITurma[]>()], asapScheduler)),
    );
  }

  getTurmaIdentifier(turma: Pick<ITurma, 'id'>): number {
    return turma.id;
  }

  compareTurma(o1: Pick<ITurma, 'id'> | null, o2: Pick<ITurma, 'id'> | null): boolean {
    return o1 && o2 ? this.getTurmaIdentifier(o1) === this.getTurmaIdentifier(o2) : o1 === o2;
  }

  addTurmaToCollectionIfMissing<Type extends Pick<ITurma, 'id'>>(
    turmaCollection: Type[],
    ...turmasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const turmas: Type[] = turmasToCheck.filter(isPresent);
    if (turmas.length > 0) {
      const turmaCollectionIdentifiers = turmaCollection.map(turmaItem => this.getTurmaIdentifier(turmaItem));
      const turmasToAdd = turmas.filter(turmaItem => {
        const turmaIdentifier = this.getTurmaIdentifier(turmaItem);
        if (turmaCollectionIdentifiers.includes(turmaIdentifier)) {
          return false;
        }
        turmaCollectionIdentifiers.push(turmaIdentifier);
        return true;
      });
      return [...turmasToAdd, ...turmaCollection];
    }
    return turmaCollection;
  }

  protected convertDateFromClient<T extends ITurma | NewTurma | PartialUpdateTurma>(turma: T): RestOf<T> {
    return {
      ...turma,
      inicio: turma.inicio?.format(DATE_FORMAT) ?? null,
      termino: turma.termino?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restTurma: RestTurma): ITurma {
    return {
      ...restTurma,
      inicio: restTurma.inicio ? dayjs(restTurma.inicio) : undefined,
      termino: restTurma.termino ? dayjs(restTurma.termino) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTurma>): HttpResponse<ITurma> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTurma[]>): HttpResponse<ITurma[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
