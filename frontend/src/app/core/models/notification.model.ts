export interface Notification {
  id: string;
  tenantId: string;
  status: NotificationStatus;
  preferredChannel: Channel;
  fallbackChannels: Channel[];
  recipientEmail?: string;
  recipientPhone?: string;
  recipientDeviceToken?: string;
  templateSubject: string;
  templateBody: string;
  createdAt: string;
  updatedAt: string;
  attempts: DeliveryAttempt[];
}

export interface DeliveryAttempt {
  id: string;
  channel: Channel;
  result: 'SUCCESS' | 'FAILED';
  errorMessage?: string;
  attemptedAt: string;
}

export enum NotificationStatus {
  PENDING = 'PENDING',
  SENDING = 'SENDING',
  DELIVERED = 'DELIVERED',
  FAILED = 'FAILED',
  EXHAUSTED = 'EXHAUSTED'
}

export enum Channel {
  EMAIL = 'EMAIL',
  SMS = 'SMS',
  PUSH = 'PUSH'
}

export interface SendNotificationRequest {
  tenantId: string;
  preferredChannel: Channel;
  fallbackChannels: Channel[];
  recipientEmail?: string;
  recipientPhone?: string;
  recipientDeviceToken?: string;
  templateSubject: string;
  templateBody: string;
  templateVariables?: Record<string, string>;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
