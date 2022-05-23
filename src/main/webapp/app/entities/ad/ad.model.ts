import { IPhoto } from 'app/entities/photo/photo.model';
import { IUser } from 'app/entities/user/user.model';
import { IPart } from 'app/entities/part/part.model';
import { Quality } from 'app/entities/enumerations/quality.model';

export interface IAd {
  id?: number;
  description?: string;
  quality?: Quality;
  price?: number;
  photos?: IPhoto[] | null;
  publisher?: IUser;
  part?: IPart | null;
}

export class Ad implements IAd {
  constructor(
    public id?: number,
    public description?: string,
    public quality?: Quality,
    public price?: number,
    public photos?: IPhoto[] | null,
    public publisher?: IUser,
    public part?: IPart | null
  ) {}
}

export function getAdIdentifier(ad: IAd): number | undefined {
  return ad.id;
}
