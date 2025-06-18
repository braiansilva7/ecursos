import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CapacitacaoResolve from './route/capacitacao-routing-resolve.service';

const capacitacaoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/capacitacao.component').then(m => m.CapacitacaoComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/capacitacao-detail.component').then(m => m.CapacitacaoDetailComponent),
    resolve: {
      capacitacao: CapacitacaoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/capacitacao-update.component').then(m => m.CapacitacaoUpdateComponent),
    resolve: {
      capacitacao: CapacitacaoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/capacitacao-update.component').then(m => m.CapacitacaoUpdateComponent),
    resolve: {
      capacitacao: CapacitacaoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default capacitacaoRoute;
