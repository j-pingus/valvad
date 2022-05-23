import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CompatibilityDetailComponent } from './compatibility-detail.component';

describe('Compatibility Management Detail Component', () => {
  let comp: CompatibilityDetailComponent;
  let fixture: ComponentFixture<CompatibilityDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CompatibilityDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ compatibility: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CompatibilityDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CompatibilityDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load compatibility on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.compatibility).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
