import { IAd } from 'app/entities/ad/ad.model';

export interface IPhoto {
  id?: number;
  binaryContentType?: string;
  binary?: string;
  description?: string;
  ad?: IAd | null;
}

export class Photo implements IPhoto {
  constructor(
    public id?: number,
    public binaryContentType?: string,
    public binary?: string,
    public description?: string,
    public ad?: IAd | null
  ) {}
}

export function getPhotoIdentifier(photo: IPhoto): number | undefined {
  return photo.id;
}
