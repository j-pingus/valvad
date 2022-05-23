import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPermit, Permit } from '../permit.model';
import { PermitService } from '../service/permit.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { Subject } from 'app/entities/enumerations/subject.model';
import { Right } from 'app/entities/enumerations/right.model';

@Component({
  selector: 'jhi-permit-update',
  templateUrl: './permit-update.component.html',
})
export class PermitUpdateComponent implements OnInit {
  isSaving = false;
  subjectValues = Object.keys(Subject);
  rightValues = Object.keys(Right);

  usersSharedCollection: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    subject: [null, [Validators.required]],
    subjectId: [null, [Validators.required]],
    right: [null, [Validators.required]],
    user: [],
  });

  constructor(
    protected permitService: PermitService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ permit }) => {
      this.updateForm(permit);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const permit = this.createFromForm();
    if (permit.id !== undefined) {
      this.subscribeToSaveResponse(this.permitService.update(permit));
    } else {
      this.subscribeToSaveResponse(this.permitService.create(permit));
    }
  }

  trackUserById(_index: number, item: IUser): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPermit>>): void {
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

  protected updateForm(permit: IPermit): void {
    this.editForm.patchValue({
      id: permit.id,
      subject: permit.subject,
      subjectId: permit.subjectId,
      right: permit.right,
      user: permit.user,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, permit.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): IPermit {
    return {
      ...new Permit(),
      id: this.editForm.get(['id'])!.value,
      subject: this.editForm.get(['subject'])!.value,
      subjectId: this.editForm.get(['subjectId'])!.value,
      right: this.editForm.get(['right'])!.value,
      user: this.editForm.get(['user'])!.value,
    };
  }
}
