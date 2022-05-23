import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IModel, getModelIdentifier } from '../model.model';

export type EntityResponseType = HttpResponse<IModel>;
export type EntityArrayResponseType = HttpResponse<IModel[]>;

@Injectable({ providedIn: 'root' })
export class ModelService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/models');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/models');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(model: IModel): Observable<EntityResponseType> {
    return this.http.post<IModel>(this.resourceUrl, model, { observe: 'response' });
  }

  update(model: IModel): Observable<EntityResponseType> {
    return this.http.put<IModel>(`${this.resourceUrl}/${getModelIdentifier(model) as number}`, model, { observe: 'response' });
  }

  partialUpdate(model: IModel): Observable<EntityResponseType> {
    return this.http.patch<IModel>(`${this.resourceUrl}/${getModelIdentifier(model) as number}`, model, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IModel>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IModel[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IModel[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addModelToCollectionIfMissing(modelCollection: IModel[], ...modelsToCheck: (IModel | null | undefined)[]): IModel[] {
    const models: IModel[] = modelsToCheck.filter(isPresent);
    if (models.length > 0) {
      const modelCollectionIdentifiers = modelCollection.map(modelItem => getModelIdentifier(modelItem)!);
      const modelsToAdd = models.filter(modelItem => {
        const modelIdentifier = getModelIdentifier(modelItem);
        if (modelIdentifier == null || modelCollectionIdentifiers.includes(modelIdentifier)) {
          return false;
        }
        modelCollectionIdentifiers.push(modelIdentifier);
        return true;
      });
      return [...modelsToAdd, ...modelCollection];
    }
    return modelCollection;
  }
}
