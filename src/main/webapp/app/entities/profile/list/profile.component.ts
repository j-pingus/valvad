import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IProfile } from '../profile.model';
import { ProfileService } from '../service/profile.service';
import { ProfileDeleteDialogComponent } from '../delete/profile-delete-dialog.component';

@Component({
  selector: 'jhi-profile',
  templateUrl: './profile.component.html',
})
export class ProfileComponent implements OnInit {
  profiles?: IProfile[];
  isLoading = false;
  currentSearch: string;

  constructor(protected profileService: ProfileService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.profileService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IProfile[]>) => {
            this.isLoading = false;
            this.profiles = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.profileService.query().subscribe({
      next: (res: HttpResponse<IProfile[]>) => {
        this.isLoading = false;
        this.profiles = res.body ?? [];
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

  trackId(_index: number, item: IProfile): number {
    return item.id!;
  }

  delete(profile: IProfile): void {
    const modalRef = this.modalService.open(ProfileDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.profile = profile;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
