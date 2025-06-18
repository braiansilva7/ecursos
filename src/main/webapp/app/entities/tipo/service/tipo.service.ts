import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITipo, NewTipo } from '../tipo.model';

export type PartialUpdateTipo = Partial<ITipo> & Pick<ITipo, 'id'>;

export type EntityResponseType = HttpResponse<ITipo>;
export type EntityArrayResponseType = HttpResponse<ITipo[]>;

@Injectable({ providedIn: 'root' })
export class TipoService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tipos');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/tipos/_search');

  create(tipo: NewTipo): Observable<EntityResponseType> {
    return this.http.post<ITipo>(this.resourceUrl, tipo, { observe: 'response' });
  }

  update(tipo: ITipo): Observable<EntityResponseType> {
    return this.http.put<ITipo>(`${this.resourceUrl}/${this.getTipoIdentifier(tipo)}`, tipo, { observe: 'response' });
  }

  partialUpdate(tipo: PartialUpdateTipo): Observable<EntityResponseType> {
    return this.http.patch<ITipo>(`${this.resourceUrl}/${this.getTipoIdentifier(tipo)}`, tipo, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITipo>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITipo[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITipo[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ITipo[]>()], asapScheduler)));
  }

  getTipoIdentifier(tipo: Pick<ITipo, 'id'>): number {
    return tipo.id;
  }

  compareTipo(o1: Pick<ITipo, 'id'> | null, o2: Pick<ITipo, 'id'> | null): boolean {
    return o1 && o2 ? this.getTipoIdentifier(o1) === this.getTipoIdentifier(o2) : o1 === o2;
  }

  addTipoToCollectionIfMissing<Type extends Pick<ITipo, 'id'>>(
    tipoCollection: Type[],
    ...tiposToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tipos: Type[] = tiposToCheck.filter(isPresent);
    if (tipos.length > 0) {
      const tipoCollectionIdentifiers = tipoCollection.map(tipoItem => this.getTipoIdentifier(tipoItem));
      const tiposToAdd = tipos.filter(tipoItem => {
        const tipoIdentifier = this.getTipoIdentifier(tipoItem);
        if (tipoCollectionIdentifiers.includes(tipoIdentifier)) {
          return false;
        }
        tipoCollectionIdentifiers.push(tipoIdentifier);
        return true;
      });
      return [...tiposToAdd, ...tipoCollection];
    }
    return tipoCollection;
  }
}
