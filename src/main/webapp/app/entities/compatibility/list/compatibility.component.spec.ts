import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CompatibilityService } from '../service/compatibility.service';

import { CompatibilityComponent } from './compatibility.component';

describe('Compatibility Management Component', () => {
  let comp: CompatibilityComponent;
  let fixture: ComponentFixture<CompatibilityComponent>;
  let service: CompatibilityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'compatibility', component: CompatibilityComponent }]), HttpClientTestingModule],
      declarations: [CompatibilityComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(CompatibilityComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CompatibilityComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CompatibilityService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.compatibilities?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
