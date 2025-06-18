import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import MilitarResolve from './route/militar-routing-resolve.service';

const militarRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/militar.component').then(m => m.MilitarComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/militar-detail.component').then(m => m.MilitarDetailComponent),
    resolve: {
      militar: MilitarResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/militar-update.component').then(m => m.MilitarUpdateComponent),
    resolve: {
      militar: MilitarResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/militar-update.component').then(m => m.MilitarUpdateComponent),
    resolve: {
      militar: MilitarResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default militarRoute;
