import dayjs from 'dayjs/esm';
import { ICurso } from 'app/entities/curso/curso.model';
import { StatusCursoEnum } from 'app/entities/enumerations/status-curso-enum.model';
import { ModalidadeEnum } from 'app/entities/enumerations/modalidade-enum.model';

export interface ITurma {
  id: number;
  inicio?: dayjs.Dayjs | null;
  termino?: dayjs.Dayjs | null;
  ano?: number | null;
  statusCurso?: keyof typeof StatusCursoEnum | null;
  modalidade?: keyof typeof ModalidadeEnum | null;
  qtdVagas?: string | null;
  numeroBca?: string | null;
  curso?: Pick<ICurso, 'id' | 'cursoNome' | 'cursoSigla' | 'empresa' | 'tipo'> | null;
}

export type NewTurma = Omit<ITurma, 'id'> & { id: null };
