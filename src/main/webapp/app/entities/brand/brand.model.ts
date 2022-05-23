import { IPart } from 'app/entities/part/part.model';

export interface IBrand {
  id?: number;
  name?: string;
  parts?: IPart[] | null;
}

export class Brand implements IBrand {
  constructor(public id?: number, public name?: string, public parts?: IPart[] | null) {}
}

export function getBrandIdentifier(brand: IBrand): number | undefined {
  return brand.id;
}
