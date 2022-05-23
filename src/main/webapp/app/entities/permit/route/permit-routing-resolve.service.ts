import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPermit, Permit } from '../permit.model';
import { PermitService } from '../service/permit.service';

@Injectable({ providedIn: 'root' })
export class PermitRoutingResolveService implements Resolve<IPermit> {
  constructor(protected service: PermitService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPermit> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((permit: HttpResponse<Permit>) => {
          if (permit.body) {
            return of(permit.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Permit());
  }
}
