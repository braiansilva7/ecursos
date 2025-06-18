import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'posto',
    data: { pageTitle: 'ecursosApp.posto.home.title' },
    loadChildren: () => import('./posto/posto.routes'),
  },
  {
    path: 'militar',
    data: { pageTitle: 'ecursosApp.militar.home.title' },
    loadChildren: () => import('./militar/militar.routes'),
  },
  {
    path: 'tipo',
    data: { pageTitle: 'ecursosApp.tipo.home.title' },
    loadChildren: () => import('./tipo/tipo.routes'),
  },
  {
    path: 'curso',
    data: { pageTitle: 'ecursosApp.curso.home.title' },
    loadChildren: () => import('./curso/curso.routes'),
  },
  {
    path: 'capacitacao',
    data: { pageTitle: 'ecursosApp.capacitacao.home.title' },
    loadChildren: () => import('./capacitacao/capacitacao.routes'),
  },
  {
    path: 'turma',
    data: { pageTitle: 'ecursosApp.turma.home.title' },
    loadChildren: () => import('./turma/turma.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
