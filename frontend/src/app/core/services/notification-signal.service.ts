import { Injectable, signal } from '@angular/core';
import { NotificationService } from './notification.service';
import { Notification, SendNotificationRequest } from '../models/notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationSignalService {
  notifications = signal<Notification[]>([]);
  totalElements = signal<number>(0);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);
  selectedNotification = signal<Notification | null>(null);

  constructor(private notificationService: NotificationService) {}

  loadNotifications(tenantId?: string, status?: string, page = 0, size = 10): void {
    this.loading.set(true);
    this.error.set(null);
    this.notificationService.getNotifications(tenantId, status, page, size).subscribe({
      next: (pageData) => {
        this.notifications.set(pageData.content);
        this.totalElements.set(pageData.totalElements);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.message || 'Failed to load notifications');
        this.loading.set(false);
      }
    });
  }

  loadNotificationById(id: string): void {
    this.loading.set(true);
    this.notificationService.getNotificationById(id).subscribe({
      next: (notif) => {
        this.selectedNotification.set(notif);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.message);
        this.loading.set(false);
      }
    });
  }

  sendNotification(request: SendNotificationRequest): void {
    this.loading.set(true);
    this.notificationService.sendNotification(request).subscribe({
      next: () => {
        this.loadNotifications(request.tenantId);
      },
      error: (err) => {
        this.error.set(err.message);
        this.loading.set(false);
      }
    });
  }

  retryNotification(id: string, tenantId: string): void {
    this.notificationService.retryNotification(id).subscribe({
      next: () => {
        this.loadNotifications(tenantId);
      },
      error: (err) => {
        this.error.set(err.message);
      }
    });
  }
}
