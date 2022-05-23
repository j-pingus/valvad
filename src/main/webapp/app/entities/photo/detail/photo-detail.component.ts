import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPhoto } from '../photo.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-photo-detail',
  templateUrl: './photo-detail.component.html',
})
export class PhotoDetailComponent implements OnInit {
  photo: IPhoto | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ photo }) => {
      this.photo = photo;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
