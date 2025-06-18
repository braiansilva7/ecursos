import dayjs from 'dayjs/esm';

import { IMilitar, NewMilitar } from './militar.model';

export const sampleWithRequiredData: IMilitar = {
  id: 5855,
  saram: 'actually nation mellow',
  nomeCompleto: 'oof demobilise',
  nomeGuerra: 'supposing',
  om: 'monopolize blurt',
  statusMilitar: 'INATIVO',
  forca: 'ORGAO_CIVIL_BRASILEIRO',
};

export const sampleWithPartialData: IMilitar = {
  id: 2344,
  saram: 'following',
  nomeCompleto: 'inasmuch',
  nomeGuerra: 'pfft off-ramp',
  om: 'through princess',
  telefone: 'onto opposite',
  statusMilitar: 'INATIVO',
  forca: 'ORGAO_CIVIL_BRASILEIRO',
  ultimaPromocao: dayjs('2024-09-18'),
  cpf: 'sleuth',
  email: 'Rafael.Macedo81@yahoo.com',
};

export const sampleWithFullData: IMilitar = {
  id: 17394,
  saram: 'weld versus gleefully',
  nomeCompleto: 'um cyclone joyful',
  nomeGuerra: 'whereas',
  om: 'indeed stain defendant',
  telefone: 'like appall rigidly',
  statusMilitar: 'TRANSFERIDO',
  forca: 'ORGAO_CIVIL_BRASILEIRO',
  nrAntiguidade: 'versus realistic frequent',
  ultimaPromocao: dayjs('2024-09-19'),
  cpf: 'lest sympathetically',
  email: 'Leonardo79@gmail.com',
};

export const sampleWithNewData: NewMilitar = {
  saram: 'where restfully',
  nomeCompleto: 'misuse',
  nomeGuerra: 'swiftly unfortunate',
  om: 'less',
  statusMilitar: 'INATIVO',
  forca: 'POLICIA_MILITAR',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
