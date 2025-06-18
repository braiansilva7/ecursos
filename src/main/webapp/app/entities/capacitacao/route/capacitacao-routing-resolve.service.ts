import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICapacitacao } from '../capacitacao.model';
import { CapacitacaoService } from '../service/capacitacao.service';

const capacitacaoResolve = (route: ActivatedRouteSnapshot): Observable<null | ICapacitacao> => {
  const id = route.params.id;
  if (id) {
    return inject(CapacitacaoService)
      .find(id)
      .pipe(
        mergeMap((capacitacao: HttpResponse<ICapacitacao>) => {
          if (capacitacao.body) {
            return of(capacitacao.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default capacitacaoResolve;
