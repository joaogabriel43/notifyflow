import { Component, OnInit, effect, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { NotificationSignalService } from '../../../core/services/notification-signal.service';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { ChannelIconComponent } from '../../../shared/components/channel-icon/channel-icon.component';
import { NotificationStatus } from '../../../core/models/notification.model';

@Component({
  selector: 'app-notification-detail',
  standalone: true,
  imports: [
    CommonModule, RouterModule, MatCardModule, MatButtonModule, 
    MatIconModule, MatDividerModule, MatSnackBarModule, MatProgressSpinnerModule,
    StatusBadgeComponent, ChannelIconComponent
  ],
  templateUrl: './notification-detail.component.html',
  styleUrl: './notification-detail.component.scss'
})
export class NotificationDetailComponent implements OnInit {
  notificationId: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public notificationState: NotificationSignalService,
    private snackBar: MatSnackBar
  ) {
    this.route.params.subscribe(params => {
      this.notificationId = params['id'];
      if (this.notificationId) {
        this.notificationState.loadNotificationById(this.notificationId);
      }
    });

    effect(() => {
      if (this.notificationState.error()) {
        this.router.navigate(['/notifications']);
      }
    });
  }

  ngOnInit(): void {}

  retry(): void {
    const notif = this.notificationState.selectedNotification();
    if (notif && confirm('Are you sure you want to retry this notification?')) {
      // Direct call without state reloading the list since we are in detail view
      this.notificationState.retryNotification(notif.id, notif.tenantId);
      this.snackBar.open('Retry initiated', 'Close', { duration: 3000 });
      // Reload this specific notification after a short delay
      setTimeout(() => this.notificationState.loadNotificationById(notif.id), 1000);
    }
  }

  canRetry(): boolean {
    const status = this.notificationState.selectedNotification()?.status;
    return status === NotificationStatus.FAILED || status === NotificationStatus.EXHAUSTED;
  }
}
