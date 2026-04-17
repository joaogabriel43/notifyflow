import { TestBed } from '@angular/core/testing';

import { NotificationSignalService } from './notification-signal.service';

describe('NotificationSignalService', () => {
  let service: NotificationSignalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificationSignalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
