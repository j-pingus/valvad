import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPhoto } from '../photo.model';
import { PhotoService } from '../service/photo.service';

@Component({
  templateUrl: './photo-delete-dialog.component.html',
})
export class PhotoDeleteDialogComponent {
  photo?: IPhoto;

  constructor(protected photoService: PhotoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.photoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
