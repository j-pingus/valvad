import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AdDetailComponent } from './ad-detail.component';

describe('Ad Management Detail Component', () => {
  let comp: AdDetailComponent;
  let fixture: ComponentFixture<AdDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ ad: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AdDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AdDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load ad on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.ad).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
