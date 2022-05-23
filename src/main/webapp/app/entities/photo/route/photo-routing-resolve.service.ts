import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPhoto, Photo } from '../photo.model';
import { PhotoService } from '../service/photo.service';

@Injectable({ providedIn: 'root' })
export class PhotoRoutingResolveService implements Resolve<IPhoto> {
  constructor(protected service: PhotoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPhoto> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((photo: HttpResponse<Photo>) => {
          if (photo.body) {
            return of(photo.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Photo());
  }
}
