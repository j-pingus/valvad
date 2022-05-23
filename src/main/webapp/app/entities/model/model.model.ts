import { ICompatibility } from 'app/entities/compatibility/compatibility.model';

export interface IModel {
  id?: number;
  name?: string;
  years?: string | null;
  compatibilities?: ICompatibility[] | null;
}

export class Model implements IModel {
  constructor(public id?: number, public name?: string, public years?: string | null, public compatibilities?: ICompatibility[] | null) {}
}

export function getModelIdentifier(model: IModel): number | undefined {
  return model.id;
}
