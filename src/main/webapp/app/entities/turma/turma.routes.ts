import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TurmaResolve from './route/turma-routing-resolve.service';

const turmaRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/turma.component').then(m => m.TurmaComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/turma-detail.component').then(m => m.TurmaDetailComponent),
    resolve: {
      turma: TurmaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/turma-update.component').then(m => m.TurmaUpdateComponent),
    resolve: {
      turma: TurmaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/turma-update.component').then(m => m.TurmaUpdateComponent),
    resolve: {
      turma: TurmaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default turmaRoute;
