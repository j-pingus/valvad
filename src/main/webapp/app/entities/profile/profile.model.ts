import { IUser } from 'app/entities/user/user.model';

export interface IProfile {
  id?: number;
  email?: string | null;
  instagram?: string | null;
  phone?: string | null;
  facebook?: string | null;
  user?: IUser | null;
}

export class Profile implements IProfile {
  constructor(
    public id?: number,
    public email?: string | null,
    public instagram?: string | null,
    public phone?: string | null,
    public facebook?: string | null,
    public user?: IUser | null
  ) {}
}

export function getProfileIdentifier(profile: IProfile): number | undefined {
  return profile.id;
}
