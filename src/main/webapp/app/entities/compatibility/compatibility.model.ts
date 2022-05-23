import { IPart } from 'app/entities/part/part.model';
import { IModel } from 'app/entities/model/model.model';

export interface ICompatibility {
  id?: number;
  part?: IPart | null;
  model?: IModel | null;
}

export class Compatibility implements ICompatibility {
  constructor(public id?: number, public part?: IPart | null, public model?: IModel | null) {}
}

export function getCompatibilityIdentifier(compatibility: ICompatibility): number | undefined {
  return compatibility.id;
}
