import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CompatibilityComponent } from '../list/compatibility.component';
import { CompatibilityDetailComponent } from '../detail/compatibility-detail.component';
import { CompatibilityUpdateComponent } from '../update/compatibility-update.component';
import { CompatibilityRoutingResolveService } from './compatibility-routing-resolve.service';

const compatibilityRoute: Routes = [
  {
    path: '',
    component: CompatibilityComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CompatibilityDetailComponent,
    resolve: {
      compatibility: CompatibilityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CompatibilityUpdateComponent,
    resolve: {
      compatibility: CompatibilityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CompatibilityUpdateComponent,
    resolve: {
      compatibility: CompatibilityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(compatibilityRoute)],
  exports: [RouterModule],
})
export class CompatibilityRoutingModule {}
