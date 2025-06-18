export interface ITipo {
  id: number;
  categoria?: string | null;
}

export type NewTipo = Omit<ITipo, 'id'> & { id: null };
