import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPermit } from '../permit.model';
import { PermitService } from '../service/permit.service';

@Component({
  templateUrl: './permit-delete-dialog.component.html',
})
export class PermitDeleteDialogComponent {
  permit?: IPermit;

  constructor(protected permitService: PermitService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.permitService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
