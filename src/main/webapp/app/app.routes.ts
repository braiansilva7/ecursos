import { Routes } from '@angular/router';

import { Authority } from 'app/config/authority.constants';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { errorRoute } from './layouts/error/error.route';
import HomeComponent from './home/home.component';
import NavbarComponent from './layouts/navbar/navbar.component';
import DashboardComponent from './layouts/dashboard/dashboard.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    title: 'home.title',
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    title: 'home.dashboard',
  },
  {
    path: '',
    component: NavbarComponent,
    outlet: 'navbar',
  },
  {
    path: 'admin',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./admin/admin.routes'),
  },
  {
    path: '',
    loadChildren: () => import(`./entities/entity.routes`),
  },
  ...errorRoute,
];

export default routes;
