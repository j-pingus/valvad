import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICompatibility, Compatibility } from '../compatibility.model';
import { CompatibilityService } from '../service/compatibility.service';
import { IPart } from 'app/entities/part/part.model';
import { PartService } from 'app/entities/part/service/part.service';
import { IModel } from 'app/entities/model/model.model';
import { ModelService } from 'app/entities/model/service/model.service';

@Component({
  selector: 'jhi-compatibility-update',
  templateUrl: './compatibility-update.component.html',
})
export class CompatibilityUpdateComponent implements OnInit {
  isSaving = false;

  partsSharedCollection: IPart[] = [];
  modelsSharedCollection: IModel[] = [];

  editForm = this.fb.group({
    id: [],
    part: [],
    model: [],
  });

  constructor(
    protected compatibilityService: CompatibilityService,
    protected partService: PartService,
    protected modelService: ModelService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ compatibility }) => {
      this.updateForm(compatibility);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const compatibility = this.createFromForm();
    if (compatibility.id !== undefined) {
      this.subscribeToSaveResponse(this.compatibilityService.update(compatibility));
    } else {
      this.subscribeToSaveResponse(this.compatibilityService.create(compatibility));
    }
  }

  trackPartById(_index: number, item: IPart): number {
    return item.id!;
  }

  trackModelById(_index: number, item: IModel): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICompatibility>>): void {
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

  protected updateForm(compatibility: ICompatibility): void {
    this.editForm.patchValue({
      id: compatibility.id,
      part: compatibility.part,
      model: compatibility.model,
    });

    this.partsSharedCollection = this.partService.addPartToCollectionIfMissing(this.partsSharedCollection, compatibility.part);
    this.modelsSharedCollection = this.modelService.addModelToCollectionIfMissing(this.modelsSharedCollection, compatibility.model);
  }

  protected loadRelationshipsOptions(): void {
    this.partService
      .query()
      .pipe(map((res: HttpResponse<IPart[]>) => res.body ?? []))
      .pipe(map((parts: IPart[]) => this.partService.addPartToCollectionIfMissing(parts, this.editForm.get('part')!.value)))
      .subscribe((parts: IPart[]) => (this.partsSharedCollection = parts));

    this.modelService
      .query()
      .pipe(map((res: HttpResponse<IModel[]>) => res.body ?? []))
      .pipe(map((models: IModel[]) => this.modelService.addModelToCollectionIfMissing(models, this.editForm.get('model')!.value)))
      .subscribe((models: IModel[]) => (this.modelsSharedCollection = models));
  }

  protected createFromForm(): ICompatibility {
    return {
      ...new Compatibility(),
      id: this.editForm.get(['id'])!.value,
      part: this.editForm.get(['part'])!.value,
      model: this.editForm.get(['model'])!.value,
    };
  }
}
