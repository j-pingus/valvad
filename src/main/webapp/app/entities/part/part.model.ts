import { ICompatibility } from 'app/entities/compatibility/compatibility.model';
import { IBrand } from 'app/entities/brand/brand.model';
import { IAd } from 'app/entities/ad/ad.model';

export interface IPart {
  id?: number;
  description?: string;
  number?: string;
  compatibility?: ICompatibility | null;
  brand?: IBrand | null;
  ads?: IAd[] | null;
}

export class Part implements IPart {
  constructor(
    public id?: number,
    public description?: string,
    public number?: string,
    public compatibility?: ICompatibility | null,
    public brand?: IBrand | null,
    public ads?: IAd[] | null
  ) {}
}

export function getPartIdentifier(part: IPart): number | undefined {
  return part.id;
}
