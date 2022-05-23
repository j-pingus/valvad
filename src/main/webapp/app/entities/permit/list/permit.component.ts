import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPermit } from '../permit.model';
import { PermitService } from '../service/permit.service';
import { PermitDeleteDialogComponent } from '../delete/permit-delete-dialog.component';

@Component({
  selector: 'jhi-permit',
  templateUrl: './permit.component.html',
})
export class PermitComponent implements OnInit {
  permits?: IPermit[];
  isLoading = false;
  currentSearch: string;

  constructor(protected permitService: PermitService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.permitService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IPermit[]>) => {
            this.isLoading = false;
            this.permits = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.permitService.query().subscribe({
      next: (res: HttpResponse<IPermit[]>) => {
        this.isLoading = false;
        this.permits = res.body ?? [];
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

  trackId(_index: number, item: IPermit): number {
    return item.id!;
  }

  delete(permit: IPermit): void {
    const modalRef = this.modalService.open(PermitDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.permit = permit;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
