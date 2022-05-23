import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ModelDetailComponent } from './model-detail.component';

describe('Model Management Detail Component', () => {
  let comp: ModelDetailComponent;
  let fixture: ComponentFixture<ModelDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ModelDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ model: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ModelDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ModelDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load model on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.model).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
