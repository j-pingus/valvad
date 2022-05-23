import { IUser } from 'app/entities/user/user.model';
import { Subject } from 'app/entities/enumerations/subject.model';

export interface IMessage {
  id?: number;
  subject?: Subject | null;
  subjectId?: number | null;
  body?: string;
  sender?: IUser;
}

export class Message implements IMessage {
  constructor(
    public id?: number,
    public subject?: Subject | null,
    public subjectId?: number | null,
    public body?: string,
    public sender?: IUser
  ) {}
}

export function getMessageIdentifier(message: IMessage): number | undefined {
  return message.id;
}
