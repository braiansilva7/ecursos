import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';
import dayjs from 'dayjs/esm';
import { DATE_FORMAT } from 'app/config/input.constants';
import { catchError, tap } from 'rxjs/operators';
import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IMilitar, NewMilitar } from '../militar.model';

export type PartialUpdateMilitar = Partial<IMilitar> & Pick<IMilitar, 'id'>;

type RestOf<T extends IMilitar | NewMilitar> = Omit<T, 'ultimaPromocao'> & {
  ultimaPromocao?: string | null;
};

export type EntityResponseType = HttpResponse<IMilitar>;
export type EntityArrayResponseType = HttpResponse<IMilitar[]>;

export type RestMilitar = RestOf<IMilitar>;

export type EntityResponseTypeSaram = HttpResponse<MilitarResponse>;

export type EntityResponseTypeFotoApi = HttpResponse<FotoApi>;

interface MilitarResponse {
  ordem: string;
  pessoa: string;
  guerra: string;
  sgOrg: string;
  perfil: string;
  posto: string;
  cpf: string;
  email: string;
  nrAntiguidade: string;
  ultimaPromocao: string;
  telefoneCel: string;
}

// export interface MilitarApi {
//   id: number;
//   cd_org: string;
//   cd_posto: string;
//   nm_guerra: string;
//   nm_pessoa: string;
//   nr_cpf: string;
//   nr_ordem: string;
//   pesfis_type: string;
//   sg_org: string;
//   nr_antiguidade: string;
//   ultima_promocao: string;
//   email: string;
// }

export interface FotoApi {
  tpArq: string;
  txNomeArq: string;
  txTamanhoArq: string;
  imFoto: string;
}

@Injectable({ providedIn: 'root' })
export class MilitarService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/militars');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/militars/_search');

  create(militar: NewMilitar): Observable<EntityResponseType> {
    return this.http.post<IMilitar>(this.resourceUrl, militar, { observe: 'response' });
  }

  update(militar: IMilitar): Observable<EntityResponseType> {
    return this.http.put<IMilitar>(`${this.resourceUrl}/${this.getMilitarIdentifier(militar)}`, militar, { observe: 'response' });
  }

  partialUpdate(militar: PartialUpdateMilitar): Observable<EntityResponseType> {
    return this.http.patch<IMilitar>(`${this.resourceUrl}/${this.getMilitarIdentifier(militar)}`, militar, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMilitar>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMilitar[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  consultaMilitarSaram(saram: string): Observable<EntityResponseTypeSaram> {
    return this.http.get<MilitarResponse>(`${this.resourceUrl}/consulta/${saram}`, { observe: 'response' });
  }

  getAllMilitarsOM(): Observable<EntityArrayResponseType> {
    return this.http.get<IMilitar[]>(`${this.resourceUrl}/om`, { observe: 'response' });
  }

  getFotoApiLocal(saram: string): Observable<string> {
    return this.http.get(`${this.resourceUrl}/foto/local/${saram}`, { responseType: 'text' });
  }

  getFotoApi(saram: string): Observable<EntityResponseTypeFotoApi> {
    return this.http.get<FotoApi>(`${this.resourceUrl}/foto/${saram}`, { observe: 'response' });
  }


  queryAll(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMilitar[]>(`${this.resourceUrl}/all`, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IMilitar[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IMilitar[]>()], asapScheduler)));
  }

  getMilitarIdentifier(militar: Pick<IMilitar, 'id'>): number {
    return militar.id;
  }

  compareMilitar(o1: Pick<IMilitar, 'id'> | null, o2: Pick<IMilitar, 'id'> | null): boolean {
    return o1 && o2 ? this.getMilitarIdentifier(o1) === this.getMilitarIdentifier(o2) : o1 === o2;
  }

  addMilitarToCollectionIfMissing<Type extends Pick<IMilitar, 'id'>>(
    militarCollection: Type[],
    ...militarsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const militars: Type[] = militarsToCheck.filter(isPresent);
    if (militars.length > 0) {
      const militarCollectionIdentifiers = militarCollection.map(militarItem => this.getMilitarIdentifier(militarItem));
      const militarsToAdd = militars.filter(militarItem => {
        const militarIdentifier = this.getMilitarIdentifier(militarItem);
        if (militarCollectionIdentifiers.includes(militarIdentifier)) {
          return false;
        }
        militarCollectionIdentifiers.push(militarIdentifier);
        return true;
      });
      return [...militarsToAdd, ...militarCollection];
    }
    return militarCollection;
  }

  convertDateFromClient<T extends IMilitar | NewMilitar | PartialUpdateMilitar>(militar: T): RestOf<T> {
        return {
          ...militar,
          ultimaPromocao: militar.ultimaPromocao?.format(DATE_FORMAT) ?? null,
        };
  }
    
  protected convertDateFromServer(restMilitar: RestMilitar): IMilitar {
        return {
          ...restMilitar,
          ultimaPromocao: restMilitar.ultimaPromocao ? dayjs(restMilitar.ultimaPromocao) : undefined,
        };
  }
  protected convertResponseFromServer(res: HttpResponse<RestMilitar>): HttpResponse<IMilitar> {
        return res.clone({
          body: res.body ? this.convertDateFromServer(res.body) : null,
        });
  }
  protected convertResponseArrayFromServer(res: HttpResponse<RestMilitar[]>): HttpResponse<IMilitar[]> {
        return res.clone({
          body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
        });
  }
  
}
