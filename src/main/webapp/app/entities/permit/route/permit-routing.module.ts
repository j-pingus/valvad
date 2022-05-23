import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PermitComponent } from '../list/permit.component';
import { PermitDetailComponent } from '../detail/permit-detail.component';
import { PermitUpdateComponent } from '../update/permit-update.component';
import { PermitRoutingResolveService } from './permit-routing-resolve.service';

const permitRoute: Routes = [
  {
    path: '',
    component: PermitComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PermitDetailComponent,
    resolve: {
      permit: PermitRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PermitUpdateComponent,
    resolve: {
      permit: PermitRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PermitUpdateComponent,
    resolve: {
      permit: PermitRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(permitRoute)],
  exports: [RouterModule],
})
export class PermitRoutingModule {}
