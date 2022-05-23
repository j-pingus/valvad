import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PermitDetailComponent } from './permit-detail.component';

describe('Permit Management Detail Component', () => {
  let comp: PermitDetailComponent;
  let fixture: ComponentFixture<PermitDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PermitDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ permit: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PermitDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PermitDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load permit on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.permit).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
