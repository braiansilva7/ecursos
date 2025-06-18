import dayjs, { Dayjs } from 'dayjs';
import { IPosto } from 'app/entities/posto/posto.model';
import { StatusMilitarEnum } from 'app/entities/enumerations/status-militar-enum.model';
import { ForcaEnum } from 'app/entities/enumerations/forca-enum.model';

export interface IMilitar {
  id: number;
  saram?: string | null;
  nomeCompleto?: string | null;
  nomeGuerra?: string | null;
  om?: string | null;
  telefone?: string | null;
  statusMilitar?: keyof typeof StatusMilitarEnum | null;
  forca?: keyof typeof ForcaEnum | null;
  nrAntiguidade?: string | null;
  ultimaPromocao?: Dayjs | null;
  cpf?: string | null;
  email?: string | null;
  posto?: Pick<IPosto, 'id' | 'descricao' | 'postoSigla'> | null;
}

export type NewMilitar = Omit<IMilitar, 'id'> & { id: null };
