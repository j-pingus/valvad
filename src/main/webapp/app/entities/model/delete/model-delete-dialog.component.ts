import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IModel } from '../model.model';
import { ModelService } from '../service/model.service';

@Component({
  templateUrl: './model-delete-dialog.component.html',
})
export class ModelDeleteDialogComponent {
  model?: IModel;

  constructor(protected modelService: ModelService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.modelService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
