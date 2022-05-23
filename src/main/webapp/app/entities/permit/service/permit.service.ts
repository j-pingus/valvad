import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IPermit, getPermitIdentifier } from '../permit.model';

export type EntityResponseType = HttpResponse<IPermit>;
export type EntityArrayResponseType = HttpResponse<IPermit[]>;

@Injectable({ providedIn: 'root' })
export class PermitService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/permits');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/permits');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(permit: IPermit): Observable<EntityResponseType> {
    return this.http.post<IPermit>(this.resourceUrl, permit, { observe: 'response' });
  }

  update(permit: IPermit): Observable<EntityResponseType> {
    return this.http.put<IPermit>(`${this.resourceUrl}/${getPermitIdentifier(permit) as number}`, permit, { observe: 'response' });
  }

  partialUpdate(permit: IPermit): Observable<EntityResponseType> {
    return this.http.patch<IPermit>(`${this.resourceUrl}/${getPermitIdentifier(permit) as number}`, permit, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPermit>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPermit[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPermit[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addPermitToCollectionIfMissing(permitCollection: IPermit[], ...permitsToCheck: (IPermit | null | undefined)[]): IPermit[] {
    const permits: IPermit[] = permitsToCheck.filter(isPresent);
    if (permits.length > 0) {
      const permitCollectionIdentifiers = permitCollection.map(permitItem => getPermitIdentifier(permitItem)!);
      const permitsToAdd = permits.filter(permitItem => {
        const permitIdentifier = getPermitIdentifier(permitItem);
        if (permitIdentifier == null || permitCollectionIdentifiers.includes(permitIdentifier)) {
          return false;
        }
        permitCollectionIdentifiers.push(permitIdentifier);
        return true;
      });
      return [...permitsToAdd, ...permitCollection];
    }
    return permitCollection;
  }
}
