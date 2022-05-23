import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IModel } from '../model.model';

@Component({
  selector: 'jhi-model-detail',
  templateUrl: './model-detail.component.html',
})
export class ModelDetailComponent implements OnInit {
  model: IModel | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ model }) => {
      this.model = model;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
