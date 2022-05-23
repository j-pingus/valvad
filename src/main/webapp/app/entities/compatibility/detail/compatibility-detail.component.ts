import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICompatibility } from '../compatibility.model';

@Component({
  selector: 'jhi-compatibility-detail',
  templateUrl: './compatibility-detail.component.html',
})
export class CompatibilityDetailComponent implements OnInit {
  compatibility: ICompatibility | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ compatibility }) => {
      this.compatibility = compatibility;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
