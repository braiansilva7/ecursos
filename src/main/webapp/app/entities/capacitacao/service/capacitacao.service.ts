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
import { ICapacitacao, NewCapacitacao } from '../capacitacao.model';

export type PartialUpdateCapacitacao = Partial<ICapacitacao> & Pick<ICapacitacao, 'id'>;

export type EntityResponseType = HttpResponse<ICapacitacao>;
export type EntityArrayResponseType = HttpResponse<ICapacitacao[]>;
export type RestCapacitacao = RestOf<ICapacitacao>;

export type NewRestCapacitacao = RestOf<NewCapacitacao>;
export type PartialUpdateRestCapacitacao = RestOf<PartialUpdateCapacitacao>;

type RestOf<T extends ICapacitacao | NewCapacitacao> = Omit<T, 'inicio' | 'termino'> & {
  inicio?: string | null;
  termino?: string | null;
};

@Injectable({ providedIn: 'root' })
export class CapacitacaoService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/capacitacaos');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/capacitacaos/_search');

  create(capacitacao: NewCapacitacao): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(capacitacao);
    return this.http
      .post<RestCapacitacao>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(capacitacao: ICapacitacao): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(capacitacao);
    return this.http
      .put<RestCapacitacao>(`${this.resourceUrl}/${this.getCapacitacaoIdentifier(capacitacao)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(capacitacao: PartialUpdateCapacitacao): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(capacitacao);
    return this.http
      .patch<RestCapacitacao>(`${this.resourceUrl}/${this.getCapacitacaoIdentifier(capacitacao)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCapacitacao>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCapacitacao[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  queryAll(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCapacitacao[]>(`${this.resourceUrl}/all`, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestCapacitacao[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),

      catchError(() => scheduled([new HttpResponse<ICapacitacao[]>()], asapScheduler)),
    );
  }

  getCapacitacaoIdentifier(capacitacao: Pick<ICapacitacao, 'id'>): number {
    return capacitacao.id;
  }

  compareCapacitacao(o1: Pick<ICapacitacao, 'id'> | null, o2: Pick<ICapacitacao, 'id'> | null): boolean {
    return o1 && o2 ? this.getCapacitacaoIdentifier(o1) === this.getCapacitacaoIdentifier(o2) : o1 === o2;
  }

  addCapacitacaoToCollectionIfMissing<Type extends Pick<ICapacitacao, 'id'>>(
    capacitacaoCollection: Type[],
    ...capacitacaosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const capacitacaos: Type[] = capacitacaosToCheck.filter(isPresent);
    if (capacitacaos.length > 0) {
      const capacitacaoCollectionIdentifiers = capacitacaoCollection.map(capacitacaoItem => this.getCapacitacaoIdentifier(capacitacaoItem));
      const capacitacaosToAdd = capacitacaos.filter(capacitacaoItem => {
        const capacitacaoIdentifier = this.getCapacitacaoIdentifier(capacitacaoItem);
        if (capacitacaoCollectionIdentifiers.includes(capacitacaoIdentifier)) {
          return false;
        }
        capacitacaoCollectionIdentifiers.push(capacitacaoIdentifier);
        return true;
      });
      return [...capacitacaosToAdd, ...capacitacaoCollection];
    }
    return capacitacaoCollection;
  }


  protected convertDateFromClient(capacitacao: ICapacitacao | NewCapacitacao): ICapacitacao | NewCapacitacao {
    return {
      ...capacitacao,
      turma: capacitacao.turma
        ? {
            ...capacitacao.turma,
            inicio: capacitacao.turma.inicio ? dayjs(capacitacao.turma.inicio) : null, // Converte para Dayjs ou null
            termino: capacitacao.turma.termino ? dayjs(capacitacao.turma.termino) : null, // Converte para Dayjs ou null
          }
        : null,
    };
  }

  protected convertDateFromServer(restCapacitacao: RestCapacitacao): ICapacitacao {
    return {
      ...restCapacitacao,
      turma: restCapacitacao.turma
        ? {
            ...restCapacitacao.turma,
            inicio: restCapacitacao.turma.inicio ? dayjs(restCapacitacao.turma.inicio) : undefined,
            termino: restCapacitacao.turma.termino ? dayjs(restCapacitacao.turma.termino) : undefined,
          }
        : null,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCapacitacao>): HttpResponse<ICapacitacao> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCapacitacao[]>): HttpResponse<ICapacitacao[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
