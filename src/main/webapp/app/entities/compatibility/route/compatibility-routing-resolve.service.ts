import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICompatibility, Compatibility } from '../compatibility.model';
import { CompatibilityService } from '../service/compatibility.service';

@Injectable({ providedIn: 'root' })
export class CompatibilityRoutingResolveService implements Resolve<ICompatibility> {
  constructor(protected service: CompatibilityService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICompatibility> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((compatibility: HttpResponse<Compatibility>) => {
          if (compatibility.body) {
            return of(compatibility.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Compatibility());
  }
}
