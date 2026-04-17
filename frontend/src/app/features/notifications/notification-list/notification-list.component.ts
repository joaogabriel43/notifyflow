import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';

import { NotificationSignalService } from '../../../core/services/notification-signal.service';
import { NotificationStatus } from '../../../core/models/notification.model';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { ChannelIconComponent } from '../../../shared/components/channel-icon/channel-icon.component';
import { NotificationStatusPipe } from '../../../shared/pipes/notification-status.pipe';
import { NotificationFormComponent } from '../notification-form/notification-form.component';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [
    CommonModule, RouterModule, FormsModule,
    MatTableModule, MatPaginatorModule, MatButtonModule, MatIconModule,
    MatSelectModule, MatInputModule, MatFormFieldModule, MatProgressSpinnerModule,
    MatDialogModule, MatSnackBarModule,
    StatusBadgeComponent, ChannelIconComponent, NotificationStatusPipe
  ],
  templateUrl: './notification-list.component.html',
  styleUrl: './notification-list.component.scss'
})
export class NotificationListComponent implements OnInit {
  displayedColumns: string[] = ['id', 'tenantId', 'preferredChannel', 'status', 'fallbackChannels', 'createdAt', 'actions'];
  
  tenantId: string = 'tenant-001';
  statusFilter: string = '';
  statuses = Object.values(NotificationStatus);
  
  pageIndex = 0;
  pageSize = 10;

  constructor(
    public notificationState: NotificationSignalService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    if (!this.tenantId) {
      this.snackBar.open('Tenant ID is required', 'Close', { duration: 3000 });
      return;
    }
    this.notificationState.loadNotifications(this.tenantId, this.statusFilter || undefined, this.pageIndex, this.pageSize);
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadData();
  }

  openNotificationForm(): void {
    const dialogRef = this.dialog.open(NotificationFormComponent, {
      width: '600px',
      data: { tenantId: this.tenantId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.snackBar.open('Notification sent successfully', 'Close', { duration: 3000 });
        this.loadData();
      }
    });
  }

  retryNotification(id: string): void {
    if (confirm('Are you sure you want to retry this notification?')) {
      this.notificationState.retryNotification(id, this.tenantId);
      this.snackBar.open('Retry initiated', 'Close', { duration: 3000 });
    }
  }

  canRetry(status: NotificationStatus | string): boolean {
    return status === NotificationStatus.FAILED || status === NotificationStatus.EXHAUSTED;
  }
}
