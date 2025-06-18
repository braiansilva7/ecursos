import { IMilitar } from 'app/entities/militar/militar.model';
import { ITurma } from 'app/entities/turma/turma.model';
import { StatusEnum } from 'app/entities/enumerations/status-enum.model';

export interface ICapacitacao {
  id: number;
  capacitacaoStatus?: keyof typeof StatusEnum | null;
  sigpes?: string | null;
  militar?: Pick<IMilitar, 'id' | 'nomeCompleto' | 'om' | 'nomeGuerra' | 'saram' | 'email' | 'posto'> | null;
  turma?: Pick<ITurma, 'id' | 'inicio' | 'termino' | 'ano' | 'numeroBca' | 'qtdVagas' | 'curso'> | null;
  badgeClass?: string;  // <- Adicione essa linha
}

export type NewCapacitacao = Omit<ICapacitacao, 'id'> & { id: null };
