import { ITipo, NewTipo } from './tipo.model';

export const sampleWithRequiredData: ITipo = {
  id: 19389,
  categoria: 'unlawful between',
};

export const sampleWithPartialData: ITipo = {
  id: 17883,
  categoria: 'single fondly nutrient',
};

export const sampleWithFullData: ITipo = {
  id: 20700,
  categoria: 'mature meh',
};

export const sampleWithNewData: NewTipo = {
  categoria: 'ugh',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
