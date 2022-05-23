import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPermit } from '../permit.model';

@Component({
  selector: 'jhi-permit-detail',
  templateUrl: './permit-detail.component.html',
})
export class PermitDetailComponent implements OnInit {
  permit: IPermit | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ permit }) => {
      this.permit = permit;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
