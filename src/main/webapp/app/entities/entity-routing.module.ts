import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'ad',
        data: { pageTitle: 'valvadApp.ad.home.title' },
        loadChildren: () => import('./ad/ad.module').then(m => m.AdModule),
      },
      {
        path: 'photo',
        data: { pageTitle: 'valvadApp.photo.home.title' },
        loadChildren: () => import('./photo/photo.module').then(m => m.PhotoModule),
      },
      {
        path: 'part',
        data: { pageTitle: 'valvadApp.part.home.title' },
        loadChildren: () => import('./part/part.module').then(m => m.PartModule),
      },
      {
        path: 'brand',
        data: { pageTitle: 'valvadApp.brand.home.title' },
        loadChildren: () => import('./brand/brand.module').then(m => m.BrandModule),
      },
      {
        path: 'model',
        data: { pageTitle: 'valvadApp.model.home.title' },
        loadChildren: () => import('./model/model.module').then(m => m.ModelModule),
      },
      {
        path: 'profile',
        data: { pageTitle: 'valvadApp.profile.home.title' },
        loadChildren: () => import('./profile/profile.module').then(m => m.ProfileModule),
      },
      {
        path: 'message',
        data: { pageTitle: 'valvadApp.message.home.title' },
        loadChildren: () => import('./message/message.module').then(m => m.MessageModule),
      },
      {
        path: 'permit',
        data: { pageTitle: 'valvadApp.permit.home.title' },
        loadChildren: () => import('./permit/permit.module').then(m => m.PermitModule),
      },
      {
        path: 'compatibility',
        data: { pageTitle: 'valvadApp.compatibility.home.title' },
        loadChildren: () => import('./compatibility/compatibility.module').then(m => m.CompatibilityModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
