import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPart, Part } from '../part.model';
import { PartService } from '../service/part.service';

@Injectable({ providedIn: 'root' })
export class PartRoutingResolveService implements Resolve<IPart> {
  constructor(protected service: PartService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPart> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((part: HttpResponse<Part>) => {
          if (part.body) {
            return of(part.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Part());
  }
}
