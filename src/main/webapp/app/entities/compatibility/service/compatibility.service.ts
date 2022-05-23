import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ICompatibility, getCompatibilityIdentifier } from '../compatibility.model';

export type EntityResponseType = HttpResponse<ICompatibility>;
export type EntityArrayResponseType = HttpResponse<ICompatibility[]>;

@Injectable({ providedIn: 'root' })
export class CompatibilityService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/compatibilities');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/compatibilities');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(compatibility: ICompatibility): Observable<EntityResponseType> {
    return this.http.post<ICompatibility>(this.resourceUrl, compatibility, { observe: 'response' });
  }

  update(compatibility: ICompatibility): Observable<EntityResponseType> {
    return this.http.put<ICompatibility>(`${this.resourceUrl}/${getCompatibilityIdentifier(compatibility) as number}`, compatibility, {
      observe: 'response',
    });
  }

  partialUpdate(compatibility: ICompatibility): Observable<EntityResponseType> {
    return this.http.patch<ICompatibility>(`${this.resourceUrl}/${getCompatibilityIdentifier(compatibility) as number}`, compatibility, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICompatibility>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICompatibility[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICompatibility[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addCompatibilityToCollectionIfMissing(
    compatibilityCollection: ICompatibility[],
    ...compatibilitiesToCheck: (ICompatibility | null | undefined)[]
  ): ICompatibility[] {
    const compatibilities: ICompatibility[] = compatibilitiesToCheck.filter(isPresent);
    if (compatibilities.length > 0) {
      const compatibilityCollectionIdentifiers = compatibilityCollection.map(
        compatibilityItem => getCompatibilityIdentifier(compatibilityItem)!
      );
      const compatibilitiesToAdd = compatibilities.filter(compatibilityItem => {
        const compatibilityIdentifier = getCompatibilityIdentifier(compatibilityItem);
        if (compatibilityIdentifier == null || compatibilityCollectionIdentifiers.includes(compatibilityIdentifier)) {
          return false;
        }
        compatibilityCollectionIdentifiers.push(compatibilityIdentifier);
        return true;
      });
      return [...compatibilitiesToAdd, ...compatibilityCollection];
    }
    return compatibilityCollection;
  }
}
