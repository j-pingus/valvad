import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IMessage, getMessageIdentifier } from '../message.model';

export type EntityResponseType = HttpResponse<IMessage>;
export type EntityArrayResponseType = HttpResponse<IMessage[]>;

@Injectable({ providedIn: 'root' })
export class MessageService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/messages');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/messages');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(message: IMessage): Observable<EntityResponseType> {
    return this.http.post<IMessage>(this.resourceUrl, message, { observe: 'response' });
  }

  update(message: IMessage): Observable<EntityResponseType> {
    return this.http.put<IMessage>(`${this.resourceUrl}/${getMessageIdentifier(message) as number}`, message, { observe: 'response' });
  }

  partialUpdate(message: IMessage): Observable<EntityResponseType> {
    return this.http.patch<IMessage>(`${this.resourceUrl}/${getMessageIdentifier(message) as number}`, message, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMessage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMessage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMessage[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addMessageToCollectionIfMissing(messageCollection: IMessage[], ...messagesToCheck: (IMessage | null | undefined)[]): IMessage[] {
    const messages: IMessage[] = messagesToCheck.filter(isPresent);
    if (messages.length > 0) {
      const messageCollectionIdentifiers = messageCollection.map(messageItem => getMessageIdentifier(messageItem)!);
      const messagesToAdd = messages.filter(messageItem => {
        const messageIdentifier = getMessageIdentifier(messageItem);
        if (messageIdentifier == null || messageCollectionIdentifiers.includes(messageIdentifier)) {
          return false;
        }
        messageCollectionIdentifiers.push(messageIdentifier);
        return true;
      });
      return [...messagesToAdd, ...messageCollection];
    }
    return messageCollection;
  }
}
