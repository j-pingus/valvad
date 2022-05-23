import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PermitComponent } from './list/permit.component';
import { PermitDetailComponent } from './detail/permit-detail.component';
import { PermitUpdateComponent } from './update/permit-update.component';
import { PermitDeleteDialogComponent } from './delete/permit-delete-dialog.component';
import { PermitRoutingModule } from './route/permit-routing.module';

@NgModule({
  imports: [SharedModule, PermitRoutingModule],
  declarations: [PermitComponent, PermitDetailComponent, PermitUpdateComponent, PermitDeleteDialogComponent],
  entryComponents: [PermitDeleteDialogComponent],
})
export class PermitModule {}
