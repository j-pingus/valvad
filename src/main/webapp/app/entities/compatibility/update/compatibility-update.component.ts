import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ICompatibility, Compatibility } from '../compatibility.model';
import { CompatibilityService } from '../service/compatibility.service';

@Component({
  selector: 'jhi-compatibility-update',
  templateUrl: './compatibility-update.component.html',
})
export class CompatibilityUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
  });

  constructor(protected compatibilityService: CompatibilityService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ compatibility }) => {
      this.updateForm(compatibility);
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
    });
  }

  protected createFromForm(): ICompatibility {
    return {
      ...new Compatibility(),
      id: this.editForm.get(['id'])!.value,
    };
  }
}
