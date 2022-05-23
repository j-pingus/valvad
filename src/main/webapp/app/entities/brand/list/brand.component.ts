import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBrand } from '../brand.model';
import { BrandService } from '../service/brand.service';
import { BrandDeleteDialogComponent } from '../delete/brand-delete-dialog.component';

@Component({
  selector: 'jhi-brand',
  templateUrl: './brand.component.html',
})
export class BrandComponent implements OnInit {
  brands?: IBrand[];
  isLoading = false;
  currentSearch: string;

  constructor(protected brandService: BrandService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.brandService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IBrand[]>) => {
            this.isLoading = false;
            this.brands = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.brandService.query().subscribe({
      next: (res: HttpResponse<IBrand[]>) => {
        this.isLoading = false;
        this.brands = res.body ?? [];
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

  trackId(_index: number, item: IBrand): number {
    return item.id!;
  }

  delete(brand: IBrand): void {
    const modalRef = this.modalService.open(BrandDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.brand = brand;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
