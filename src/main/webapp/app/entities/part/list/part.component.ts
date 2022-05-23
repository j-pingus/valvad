import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPart } from '../part.model';
import { PartService } from '../service/part.service';
import { PartDeleteDialogComponent } from '../delete/part-delete-dialog.component';

@Component({
  selector: 'jhi-part',
  templateUrl: './part.component.html',
})
export class PartComponent implements OnInit {
  parts?: IPart[];
  isLoading = false;
  currentSearch: string;

  constructor(protected partService: PartService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.partService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IPart[]>) => {
            this.isLoading = false;
            this.parts = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.partService.query().subscribe({
      next: (res: HttpResponse<IPart[]>) => {
        this.isLoading = false;
        this.parts = res.body ?? [];
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

  trackId(_index: number, item: IPart): number {
    return item.id!;
  }

  delete(part: IPart): void {
    const modalRef = this.modalService.open(PartDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.part = part;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
