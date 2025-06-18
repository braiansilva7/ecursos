import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TipoResolve from './route/tipo-routing-resolve.service';

const tipoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/tipo.component').then(m => m.TipoComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/tipo-detail.component').then(m => m.TipoDetailComponent),
    resolve: {
      tipo: TipoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/tipo-update.component').then(m => m.TipoUpdateComponent),
    resolve: {
      tipo: TipoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/tipo-update.component').then(m => m.TipoUpdateComponent),
    resolve: {
      tipo: TipoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default tipoRoute;
