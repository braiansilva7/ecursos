import { ITipo } from 'app/entities/tipo/tipo.model';

export interface ICurso {
  id: number;
  cursoNome?: string | null;
  cursoSigla?: string | null;
  empresa?: string | null;
  tipo?: Pick<ITipo, 'id' | 'categoria'> | null;
}

export type NewCurso = Omit<ICurso, 'id'> & { id: null };
