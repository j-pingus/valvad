import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IAd, Ad } from '../ad.model';
import { AdService } from '../service/ad.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IPart } from 'app/entities/part/part.model';
import { PartService } from 'app/entities/part/service/part.service';
import { Quality } from 'app/entities/enumerations/quality.model';

@Component({
  selector: 'jhi-ad-update',
  templateUrl: './ad-update.component.html',
})
export class AdUpdateComponent implements OnInit {
  isSaving = false;
  qualityValues = Object.keys(Quality);

  usersSharedCollection: IUser[] = [];
  partsSharedCollection: IPart[] = [];

  editForm = this.fb.group({
    id: [],
    description: [null, [Validators.required]],
    quality: [null, [Validators.required]],
    price: [null, [Validators.required]],
    publisher: [null, Validators.required],
    part: [],
  });

  constructor(
    protected adService: AdService,
    protected userService: UserService,
    protected partService: PartService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ad }) => {
      this.updateForm(ad);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ad = this.createFromForm();
    if (ad.id !== undefined) {
      this.subscribeToSaveResponse(this.adService.update(ad));
    } else {
      this.subscribeToSaveResponse(this.adService.create(ad));
    }
  }

  trackUserById(_index: number, item: IUser): number {
    return item.id!;
  }

  trackPartById(_index: number, item: IPart): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAd>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(ad: IAd): void {
    this.editForm.patchValue({
      id: ad.id,
      description: ad.description,
      quality: ad.quality,
      price: ad.price,
      publisher: ad.publisher,
      part: ad.part,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, ad.publisher);
    this.partsSharedCollection = this.partService.addPartToCollectionIfMissing(this.partsSharedCollection, ad.part);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('publisher')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.partService
      .query()
      .pipe(map((res: HttpResponse<IPart[]>) => res.body ?? []))
      .pipe(map((parts: IPart[]) => this.partService.addPartToCollectionIfMissing(parts, this.editForm.get('part')!.value)))
      .subscribe((parts: IPart[]) => (this.partsSharedCollection = parts));
  }

  protected createFromForm(): IAd {
    return {
      ...new Ad(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      quality: this.editForm.get(['quality'])!.value,
      price: this.editForm.get(['price'])!.value,
      publisher: this.editForm.get(['publisher'])!.value,
      part: this.editForm.get(['part'])!.value,
    };
  }
}
