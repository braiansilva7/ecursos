import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITipo } from '../tipo.model';
import { TipoService } from '../service/tipo.service';

const tipoResolve = (route: ActivatedRouteSnapshot): Observable<null | ITipo> => {
  const id = route.params.id;
  if (id) {
    return inject(TipoService)
      .find(id)
      .pipe(
        mergeMap((tipo: HttpResponse<ITipo>) => {
          if (tipo.body) {
            return of(tipo.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default tipoResolve;
