import { Pipe, PipeTransform } from '@angular/core';
import { NotificationStatus } from '../../core/models/notification.model';

@Pipe({
  name: 'notificationStatus',
  standalone: true
})
export class NotificationStatusPipe implements PipeTransform {
  transform(value: NotificationStatus | string): string {
    if (!value) return '';
    return value.toString().replace(/_/g, ' ');
  }
}
