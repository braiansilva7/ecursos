import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPosto, NewPosto } from '../posto.model';

export type PartialUpdatePosto = Partial<IPosto> & Pick<IPosto, 'id'>;

export type EntityResponseType = HttpResponse<IPosto>;
export type EntityArrayResponseType = HttpResponse<IPosto[]>;

@Injectable({ providedIn: 'root' })
export class PostoService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/postos');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/postos/_search');

  create(posto: NewPosto): Observable<EntityResponseType> {
    return this.http.post<IPosto>(this.resourceUrl, posto, { observe: 'response' });
  }

  update(posto: IPosto): Observable<EntityResponseType> {
    return this.http.put<IPosto>(`${this.resourceUrl}/${this.getPostoIdentifier(posto)}`, posto, { observe: 'response' });
  }

  partialUpdate(posto: PartialUpdatePosto): Observable<EntityResponseType> {
    return this.http.patch<IPosto>(`${this.resourceUrl}/${this.getPostoIdentifier(posto)}`, posto, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPosto>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPosto[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPosto[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IPosto[]>()], asapScheduler)));
  }

  getPostoIdentifier(posto: Pick<IPosto, 'id'>): number {
    return posto.id;
  }

  comparePosto(o1: Pick<IPosto, 'id'> | null, o2: Pick<IPosto, 'id'> | null): boolean {
    return o1 && o2 ? this.getPostoIdentifier(o1) === this.getPostoIdentifier(o2) : o1 === o2;
  }

  addPostoToCollectionIfMissing<Type extends Pick<IPosto, 'id'>>(
    postoCollection: Type[],
    ...postosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const postos: Type[] = postosToCheck.filter(isPresent);
    if (postos.length > 0) {
      const postoCollectionIdentifiers = postoCollection.map(postoItem => this.getPostoIdentifier(postoItem));
      const postosToAdd = postos.filter(postoItem => {
        const postoIdentifier = this.getPostoIdentifier(postoItem);
        if (postoCollectionIdentifiers.includes(postoIdentifier)) {
          return false;
        }
        postoCollectionIdentifiers.push(postoIdentifier);
        return true;
      });
      return [...postosToAdd, ...postoCollection];
    }
    return postoCollection;
  }
}
