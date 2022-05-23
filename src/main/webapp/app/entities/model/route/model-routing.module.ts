import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ModelComponent } from '../list/model.component';
import { ModelDetailComponent } from '../detail/model-detail.component';
import { ModelUpdateComponent } from '../update/model-update.component';
import { ModelRoutingResolveService } from './model-routing-resolve.service';

const modelRoute: Routes = [
  {
    path: '',
    component: ModelComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ModelDetailComponent,
    resolve: {
      model: ModelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ModelUpdateComponent,
    resolve: {
      model: ModelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ModelUpdateComponent,
    resolve: {
      model: ModelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(modelRoute)],
  exports: [RouterModule],
})
export class ModelRoutingModule {}
