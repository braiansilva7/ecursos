import { ICapacitacao, NewCapacitacao } from './capacitacao.model';

export const sampleWithRequiredData: ICapacitacao = {
  id: 27597,
  capacitacaoStatus: 'NAO_APROVADO_PELA_DTI_COMGAP',
};

export const sampleWithPartialData: ICapacitacao = {
  id: 7270,
  capacitacaoStatus: 'NAO_APROVADO_PELA_DTI_COMGAP',
};

export const sampleWithFullData: ICapacitacao = {
  id: 26867,
  capacitacaoStatus: 'NAO_APROVADO_PELA_DTI_COMGAP',
  sigpes: 'replay leaven pro',
};

export const sampleWithNewData: NewCapacitacao = {
  capacitacaoStatus: 'APROVADO',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
