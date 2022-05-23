import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IPart, getPartIdentifier } from '../part.model';

export type EntityResponseType = HttpResponse<IPart>;
export type EntityArrayResponseType = HttpResponse<IPart[]>;

@Injectable({ providedIn: 'root' })
export class PartService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/parts');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/parts');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(part: IPart): Observable<EntityResponseType> {
    return this.http.post<IPart>(this.resourceUrl, part, { observe: 'response' });
  }

  update(part: IPart): Observable<EntityResponseType> {
    return this.http.put<IPart>(`${this.resourceUrl}/${getPartIdentifier(part) as number}`, part, { observe: 'response' });
  }

  partialUpdate(part: IPart): Observable<EntityResponseType> {
    return this.http.patch<IPart>(`${this.resourceUrl}/${getPartIdentifier(part) as number}`, part, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPart>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPart[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPart[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addPartToCollectionIfMissing(partCollection: IPart[], ...partsToCheck: (IPart | null | undefined)[]): IPart[] {
    const parts: IPart[] = partsToCheck.filter(isPresent);
    if (parts.length > 0) {
      const partCollectionIdentifiers = partCollection.map(partItem => getPartIdentifier(partItem)!);
      const partsToAdd = parts.filter(partItem => {
        const partIdentifier = getPartIdentifier(partItem);
        if (partIdentifier == null || partCollectionIdentifiers.includes(partIdentifier)) {
          return false;
        }
        partCollectionIdentifiers.push(partIdentifier);
        return true;
      });
      return [...partsToAdd, ...partCollection];
    }
    return partCollection;
  }
}
