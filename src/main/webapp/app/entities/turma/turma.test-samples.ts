import dayjs from 'dayjs/esm';

import { ITurma, NewTurma } from './turma.model';

export const sampleWithRequiredData: ITurma = {
  id: 28817,
  statusCurso: 'CONCLUIDO',
  modalidade: 'PRESENCIAL',
};

export const sampleWithPartialData: ITurma = {
  id: 3477,
  inicio: dayjs('2024-12-19'),
  termino: dayjs('2024-12-20'),
  statusCurso: 'AGUARDANDO_BCA_APROVACAO',
  modalidade: 'EAD',
  numeroBca: 'flawed beneath select',
};

export const sampleWithFullData: ITurma = {
  id: 20444,
  inicio: dayjs('2024-12-20'),
  termino: dayjs('2024-12-19'),
  ano: 6358,
  statusCurso: 'AGUARDANDO_BCA_APROVACAO',
  modalidade: 'PRESENCIAL',
  qtdVagas: 'excitedly why',
  numeroBca: 'um meanwhile',
};

export const sampleWithNewData: NewTurma = {
  statusCurso: 'CONCLUIDO',
  modalidade: 'ONLINE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
