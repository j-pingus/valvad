import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CompatibilityComponent } from './list/compatibility.component';
import { CompatibilityDetailComponent } from './detail/compatibility-detail.component';
import { CompatibilityUpdateComponent } from './update/compatibility-update.component';
import { CompatibilityDeleteDialogComponent } from './delete/compatibility-delete-dialog.component';
import { CompatibilityRoutingModule } from './route/compatibility-routing.module';

@NgModule({
  imports: [SharedModule, CompatibilityRoutingModule],
  declarations: [CompatibilityComponent, CompatibilityDetailComponent, CompatibilityUpdateComponent, CompatibilityDeleteDialogComponent],
  entryComponents: [CompatibilityDeleteDialogComponent],
})
export class CompatibilityModule {}
