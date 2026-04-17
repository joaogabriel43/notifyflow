import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NotificationService } from './notification.service';
import { Channel, NotificationStatus, SendNotificationRequest } from '../models/notification.model';

describe('NotificationService', () => {
  let service: NotificationService;
  let httpMock: HttpTestingController;
  const API_URL = 'http://localhost:8080/api/v1/notifications';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [NotificationService]
    });
    service = TestBed.inject(NotificationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getNotifications should call correct endpoint with params', () => {
    const mockPage = { content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 };
    service.getNotifications('tenant-001', 'PENDING', 0, 10).subscribe(response => {
      expect(response).toEqual(mockPage);
    });

    const req = httpMock.expectOne(r =>
      r.url === API_URL &&
      r.params.get('tenantId') === 'tenant-001' &&
      r.params.get('status') === 'PENDING'
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockPage);
  });

  it('sendNotification should POST to correct endpoint', () => {
    const request: SendNotificationRequest = {
      tenantId: 'tenant-001',
      preferredChannel: Channel.EMAIL,
      fallbackChannels: [],
      recipientEmail: 'test@example.com',
      templateSubject: 'Test Subject',
      templateBody: 'Test body'
    };

    const mockNotification = { id: '123', ...request, status: NotificationStatus.PENDING, attempts: [], createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() };

    service.sendNotification(request).subscribe(response => {
      expect(response.id).toBe('123');
    });

    const req = httpMock.expectOne(API_URL);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(mockNotification as any);
  });
});
