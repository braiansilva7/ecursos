import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PostoResolve from './route/posto-routing-resolve.service';

const postoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/posto.component').then(m => m.PostoComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/posto-detail.component').then(m => m.PostoDetailComponent),
    resolve: {
      posto: PostoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/posto-update.component').then(m => m.PostoUpdateComponent),
    resolve: {
      posto: PostoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/posto-update.component').then(m => m.PostoUpdateComponent),
    resolve: {
      posto: PostoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default postoRoute;
