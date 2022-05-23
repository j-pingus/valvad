import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IModel, Model } from '../model.model';
import { ModelService } from '../service/model.service';

@Component({
  selector: 'jhi-model-update',
  templateUrl: './model-update.component.html',
})
export class ModelUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    years: [],
  });

  constructor(protected modelService: ModelService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ model }) => {
      this.updateForm(model);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const model = this.createFromForm();
    if (model.id !== undefined) {
      this.subscribeToSaveResponse(this.modelService.update(model));
    } else {
      this.subscribeToSaveResponse(this.modelService.create(model));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IModel>>): void {
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

  protected updateForm(model: IModel): void {
    this.editForm.patchValue({
      id: model.id,
      name: model.name,
      years: model.years,
    });
  }

  protected createFromForm(): IModel {
    return {
      ...new Model(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      years: this.editForm.get(['years'])!.value,
    };
  }
}
