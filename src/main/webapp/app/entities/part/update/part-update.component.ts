import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPart, Part } from '../part.model';
import { PartService } from '../service/part.service';
import { ICompatibility } from 'app/entities/compatibility/compatibility.model';
import { CompatibilityService } from 'app/entities/compatibility/service/compatibility.service';
import { IBrand } from 'app/entities/brand/brand.model';
import { BrandService } from 'app/entities/brand/service/brand.service';

@Component({
  selector: 'jhi-part-update',
  templateUrl: './part-update.component.html',
})
export class PartUpdateComponent implements OnInit {
  isSaving = false;

  compatibilitiesSharedCollection: ICompatibility[] = [];
  brandsSharedCollection: IBrand[] = [];

  editForm = this.fb.group({
    id: [],
    description: [null, [Validators.required]],
    number: [null, [Validators.required]],
    compatibility: [],
    brand: [],
  });

  constructor(
    protected partService: PartService,
    protected compatibilityService: CompatibilityService,
    protected brandService: BrandService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ part }) => {
      this.updateForm(part);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const part = this.createFromForm();
    if (part.id !== undefined) {
      this.subscribeToSaveResponse(this.partService.update(part));
    } else {
      this.subscribeToSaveResponse(this.partService.create(part));
    }
  }

  trackCompatibilityById(_index: number, item: ICompatibility): number {
    return item.id!;
  }

  trackBrandById(_index: number, item: IBrand): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPart>>): void {
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

  protected updateForm(part: IPart): void {
    this.editForm.patchValue({
      id: part.id,
      description: part.description,
      number: part.number,
      compatibility: part.compatibility,
      brand: part.brand,
    });

    this.compatibilitiesSharedCollection = this.compatibilityService.addCompatibilityToCollectionIfMissing(
      this.compatibilitiesSharedCollection,
      part.compatibility
    );
    this.brandsSharedCollection = this.brandService.addBrandToCollectionIfMissing(this.brandsSharedCollection, part.brand);
  }

  protected loadRelationshipsOptions(): void {
    this.compatibilityService
      .query()
      .pipe(map((res: HttpResponse<ICompatibility[]>) => res.body ?? []))
      .pipe(
        map((compatibilities: ICompatibility[]) =>
          this.compatibilityService.addCompatibilityToCollectionIfMissing(compatibilities, this.editForm.get('compatibility')!.value)
        )
      )
      .subscribe((compatibilities: ICompatibility[]) => (this.compatibilitiesSharedCollection = compatibilities));

    this.brandService
      .query()
      .pipe(map((res: HttpResponse<IBrand[]>) => res.body ?? []))
      .pipe(map((brands: IBrand[]) => this.brandService.addBrandToCollectionIfMissing(brands, this.editForm.get('brand')!.value)))
      .subscribe((brands: IBrand[]) => (this.brandsSharedCollection = brands));
  }

  protected createFromForm(): IPart {
    return {
      ...new Part(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      number: this.editForm.get(['number'])!.value,
      compatibility: this.editForm.get(['compatibility'])!.value,
      brand: this.editForm.get(['brand'])!.value,
    };
  }
}
