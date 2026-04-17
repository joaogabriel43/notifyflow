import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StatusBadgeComponent } from './status-badge.component';
import { NotificationStatus } from '../../../core/models/notification.model';

describe('StatusBadgeComponent', () => {
  let component: StatusBadgeComponent;
  let fixture: ComponentFixture<StatusBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatusBadgeComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(StatusBadgeComponent);
    component = fixture.componentInstance;
    component.status = NotificationStatus.PENDING;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return badge-pending class for PENDING status', () => {
    component.status = NotificationStatus.PENDING;
    expect(component.badgeClass).toBe('badge-pending');
  });

  it('should return badge-sending class for SENDING status', () => {
    component.status = NotificationStatus.SENDING;
    expect(component.badgeClass).toBe('badge-sending');
  });

  it('should return badge-delivered class for DELIVERED status', () => {
    component.status = NotificationStatus.DELIVERED;
    expect(component.badgeClass).toBe('badge-delivered');
  });

  it('should return badge-failed class for FAILED status', () => {
    component.status = NotificationStatus.FAILED;
    expect(component.badgeClass).toBe('badge-failed');
  });

  it('should return badge-exhausted class for EXHAUSTED status', () => {
    component.status = NotificationStatus.EXHAUSTED;
    expect(component.badgeClass).toBe('badge-exhausted');
  });

  it('should render the status text', () => {
    component.status = NotificationStatus.DELIVERED;
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('DELIVERED');
  });
});
