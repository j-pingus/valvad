import { IPart } from 'app/entities/part/part.model';
import { IModel } from 'app/entities/model/model.model';

export interface ICompatibility {
  id?: number;
  parts?: IPart[] | null;
  models?: IModel[] | null;
}

export class Compatibility implements ICompatibility {
  constructor(public id?: number, public parts?: IPart[] | null, public models?: IModel[] | null) {}
}

export function getCompatibilityIdentifier(compatibility: ICompatibility): number | undefined {
  return compatibility.id;
}
