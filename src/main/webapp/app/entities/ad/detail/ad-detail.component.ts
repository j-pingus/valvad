import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAd } from '../ad.model';

@Component({
  selector: 'jhi-ad-detail',
  templateUrl: './ad-detail.component.html',
})
export class AdDetailComponent implements OnInit {
  ad: IAd | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ad }) => {
      this.ad = ad;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
