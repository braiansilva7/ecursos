import { ICurso, NewCurso } from './curso.model';

export const sampleWithRequiredData: ICurso = {
  id: 14327,
  cursoNome: 'or genuine',
  cursoSigla: 'amidst offer',
  empresa: 'bulky qua',
};

export const sampleWithPartialData: ICurso = {
  id: 1068,
  cursoNome: 'left',
  cursoSigla: 'colorless worth',
  empresa: 'triple lovingly textbook',
};

export const sampleWithFullData: ICurso = {
  id: 5466,
  cursoNome: 'yearly refurbish boohoo',
  cursoSigla: 'emotional pro concerning',
  empresa: 'tempo corny for',
};

export const sampleWithNewData: NewCurso = {
  cursoNome: 'degenerate',
  cursoSigla: 'cloves',
  empresa: 'brightly phew throughout',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
