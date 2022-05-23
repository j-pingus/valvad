import { IUser } from 'app/entities/user/user.model';
import { Subject } from 'app/entities/enumerations/subject.model';
import { Right } from 'app/entities/enumerations/right.model';

export interface IPermit {
  id?: number;
  subject?: Subject;
  subjectId?: number;
  right?: Right;
  user?: IUser | null;
}

export class Permit implements IPermit {
  constructor(public id?: number, public subject?: Subject, public subjectId?: number, public right?: Right, public user?: IUser | null) {}
}

export function getPermitIdentifier(permit: IPermit): number | undefined {
  return permit.id;
}
