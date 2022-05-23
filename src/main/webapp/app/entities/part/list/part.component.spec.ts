import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PartService } from '../service/part.service';

import { PartComponent } from './part.component';

describe('Part Management Component', () => {
  let comp: PartComponent;
  let fixture: ComponentFixture<PartComponent>;
  let service: PartService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'part', component: PartComponent }]), HttpClientTestingModule],
      declarations: [PartComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(PartComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PartComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PartService);

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
    expect(comp.parts?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
