import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationStatus } from '../../../core/models/notification.model';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './status-badge.component.html',
  styleUrl: './status-badge.component.scss'
})
export class StatusBadgeComponent {
  @Input({ required: true }) status!: NotificationStatus | string;

  get badgeClass(): string {
    switch (this.status) {
      case NotificationStatus.PENDING:
        return 'badge-pending';
      case NotificationStatus.SENDING:
        return 'badge-sending';
      case NotificationStatus.DELIVERED:
        return 'badge-delivered';
      case NotificationStatus.FAILED:
        return 'badge-failed';
      case NotificationStatus.EXHAUSTED:
        return 'badge-exhausted';
      default:
        return 'badge-default';
    }
  }
}
