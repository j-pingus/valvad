import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICompatibility } from '../compatibility.model';
import { CompatibilityService } from '../service/compatibility.service';

@Component({
  templateUrl: './compatibility-delete-dialog.component.html',
})
export class CompatibilityDeleteDialogComponent {
  compatibility?: ICompatibility;

  constructor(protected compatibilityService: CompatibilityService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.compatibilityService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
