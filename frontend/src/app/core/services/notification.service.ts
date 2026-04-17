import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';


import { Notification, Page, SendNotificationRequest, DeliveryAttempt } from '../models/notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = `${environment.apiUrl}/api/v1/notifications`;

  constructor(private http: HttpClient) {}

  getNotifications(tenantId?: string, status?: string, page: number = 0, size: number = 20): Observable<Page<Notification>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (tenantId && tenantId.trim()) {
      params = params.set('tenantId', tenantId.trim());
    }

    if (status && status !== 'All') {
      params = params.set('status', status);
    }
    return this.http.get<Page<Notification>>(this.apiUrl, { params });
  }

  getNotificationById(id: string): Observable<Notification> {
    return this.http.get<Notification>(`${this.apiUrl}/${id}`);
  }

  sendNotification(request: SendNotificationRequest): Observable<Notification> {
    return this.http.post<Notification>(this.apiUrl, request);
  }

  retryNotification(id: string): Observable<Notification> {
    return this.http.post<Notification>(`${this.apiUrl}/${id}/retry`, {});
  }

  getAttempts(id: string): Observable<DeliveryAttempt[]> {
    return this.http.get<DeliveryAttempt[]>(`${this.apiUrl}/${id}/attempts`);
  }
}
