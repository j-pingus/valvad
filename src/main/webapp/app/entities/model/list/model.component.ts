import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IModel } from '../model.model';
import { ModelService } from '../service/model.service';
import { ModelDeleteDialogComponent } from '../delete/model-delete-dialog.component';

@Component({
  selector: 'jhi-model',
  templateUrl: './model.component.html',
})
export class ModelComponent implements OnInit {
  models?: IModel[];
  isLoading = false;
  currentSearch: string;

  constructor(protected modelService: ModelService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.modelService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IModel[]>) => {
            this.isLoading = false;
            this.models = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.modelService.query().subscribe({
      next: (res: HttpResponse<IModel[]>) => {
        this.isLoading = false;
        this.models = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IModel): number {
    return item.id!;
  }

  delete(model: IModel): void {
    const modalRef = this.modalService.open(ModelDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.model = model;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
