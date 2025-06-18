import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMilitar } from '../militar.model';
import { MilitarService } from '../service/militar.service';

const militarResolve = (route: ActivatedRouteSnapshot): Observable<null | IMilitar> => {
  const id = route.params.id;
  if (id) {
    return inject(MilitarService)
      .find(id)
      .pipe(
        mergeMap((militar: HttpResponse<IMilitar>) => {
          if (militar.body) {
            return of(militar.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default militarResolve;
