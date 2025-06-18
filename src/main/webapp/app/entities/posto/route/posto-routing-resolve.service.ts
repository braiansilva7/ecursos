import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPosto } from '../posto.model';
import { PostoService } from '../service/posto.service';

const postoResolve = (route: ActivatedRouteSnapshot): Observable<null | IPosto> => {
  const id = route.params.id;
  if (id) {
    return inject(PostoService)
      .find(id)
      .pipe(
        mergeMap((posto: HttpResponse<IPosto>) => {
          if (posto.body) {
            return of(posto.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default postoResolve;
