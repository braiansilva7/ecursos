import { IPosto, NewPosto } from './posto.model';

export const sampleWithRequiredData: IPosto = {
  id: 9980,
  postoSigla: 'melodic untidy',
  descricao: 'because',
  prioridade: 7828,
  orgao: 29896,
};

export const sampleWithPartialData: IPosto = {
  id: 16458,
  postoSigla: 'quiet house',
  descricao: 'civilisation',
  prioridade: 3135,
  orgao: 5005,
};

export const sampleWithFullData: IPosto = {
  id: 30996,
  postoSigla: 'throughout',
  descricao: 'anenst',
  prioridade: 129,
  orgao: 23463,
  codSigpes: 26288,
};

export const sampleWithNewData: NewPosto = {
  postoSigla: 'gah singer fragrant',
  descricao: 'pfft jaggedly',
  prioridade: 19052,
  orgao: 32442,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
