import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICompatibility } from '../compatibility.model';
import { CompatibilityService } from '../service/compatibility.service';
import { CompatibilityDeleteDialogComponent } from '../delete/compatibility-delete-dialog.component';

@Component({
  selector: 'jhi-compatibility',
  templateUrl: './compatibility.component.html',
})
export class CompatibilityComponent implements OnInit {
  compatibilities?: ICompatibility[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected compatibilityService: CompatibilityService,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.compatibilityService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<ICompatibility[]>) => {
            this.isLoading = false;
            this.compatibilities = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.compatibilityService.query().subscribe({
      next: (res: HttpResponse<ICompatibility[]>) => {
        this.isLoading = false;
        this.compatibilities = res.body ?? [];
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

  trackId(_index: number, item: ICompatibility): number {
    return item.id!;
  }

  delete(compatibility: ICompatibility): void {
    const modalRef = this.modalService.open(CompatibilityDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.compatibility = compatibility;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
