import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { MessageService } from '../service/message.service';

import { MessageComponent } from './message.component';

describe('Message Management Component', () => {
  let comp: MessageComponent;
  let fixture: ComponentFixture<MessageComponent>;
  let service: MessageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'message', component: MessageComponent }]), HttpClientTestingModule],
      declarations: [MessageComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(MessageComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MessageComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MessageService);

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
    expect(comp.messages?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
