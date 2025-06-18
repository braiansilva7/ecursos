import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CursoResolve from './route/curso-routing-resolve.service';

const cursoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/curso.component').then(m => m.CursoComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/curso-detail.component').then(m => m.CursoDetailComponent),
    resolve: {
      curso: CursoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/curso-update.component').then(m => m.CursoUpdateComponent),
    resolve: {
      curso: CursoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/curso-update.component').then(m => m.CursoUpdateComponent),
    resolve: {
      curso: CursoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default cursoRoute;
