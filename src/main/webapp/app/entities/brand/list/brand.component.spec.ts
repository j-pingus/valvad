import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { BrandService } from '../service/brand.service';

import { BrandComponent } from './brand.component';

describe('Brand Management Component', () => {
  let comp: BrandComponent;
  let fixture: ComponentFixture<BrandComponent>;
  let service: BrandService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'brand', component: BrandComponent }]), HttpClientTestingModule],
      declarations: [BrandComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(BrandComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BrandComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(BrandService);

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
    expect(comp.brands?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
