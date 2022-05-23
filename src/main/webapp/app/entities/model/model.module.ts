import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ModelComponent } from './list/model.component';
import { ModelDetailComponent } from './detail/model-detail.component';
import { ModelUpdateComponent } from './update/model-update.component';
import { ModelDeleteDialogComponent } from './delete/model-delete-dialog.component';
import { ModelRoutingModule } from './route/model-routing.module';

@NgModule({
  imports: [SharedModule, ModelRoutingModule],
  declarations: [ModelComponent, ModelDetailComponent, ModelUpdateComponent, ModelDeleteDialogComponent],
  entryComponents: [ModelDeleteDialogComponent],
})
export class ModelModule {}
