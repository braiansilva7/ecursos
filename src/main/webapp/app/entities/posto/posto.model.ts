export interface IPosto {
  id: number;
  postoSigla?: string | null;
  descricao?: string | null;
  prioridade?: number | null;
  orgao?: number | null;
  codSigpes?: number | null;
}

export type NewPosto = Omit<IPosto, 'id'> & { id: null };
