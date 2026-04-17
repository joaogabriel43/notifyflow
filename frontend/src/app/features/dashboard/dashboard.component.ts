import { Component, OnInit, effect, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxEchartsModule } from 'ngx-echarts';
import { EChartsOption } from 'echarts';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { NotificationSignalService } from '../../core/services/notification-signal.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';
import { ChannelIconComponent } from '../../shared/components/channel-icon/channel-icon.component';
import { NotificationStatus, Channel } from '../../core/models/notification.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NgxEchartsModule, MatCardModule, MatTableModule, StatusBadgeComponent, ChannelIconComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  displayedColumns: string[] = ['id', 'tenant', 'channel', 'status', 'date'];

  totalNotifications = 0;
  deliveryRate = 0;
  failures = 0;
  pending = 0;

  chartOption: EChartsOption = {};

  notificationState = inject(NotificationSignalService);

  constructor() {
    effect(() => {
      const notifs = this.notificationState.notifications();
      const total = this.notificationState.totalElements();
      this.calculateMetrics(notifs, total);
      this.updateChart(notifs);
    });
  }

  ngOnInit(): void {
    this.notificationState.loadNotifications(undefined, undefined, 0, 100);
  }

  private calculateMetrics(notifs: any[], total: number): void {
    this.totalNotifications = total;
    if (this.totalNotifications === 0) {
      this.deliveryRate = 0;
      this.failures = 0;
      this.pending = 0;
      return;
    }

    const delivered = notifs.filter((n: any) => n.status === NotificationStatus.DELIVERED).length;
    this.failures = notifs.filter((n: any) => n.status === NotificationStatus.FAILED || n.status === NotificationStatus.EXHAUSTED).length;
    this.pending = notifs.filter((n: any) => n.status === NotificationStatus.PENDING || n.status === NotificationStatus.SENDING).length;
    
    // We only have the first 100 here, so deliveryRate will be based on the loaded sample 
    // unless we have a specific count from backend.
    this.deliveryRate = Math.round((delivered / notifs.length) * 100);
  }

  private updateChart(notifs: any[]): void {
    const emailSuccess = notifs.filter((n: any) => n.preferredChannel === Channel.EMAIL && n.status === NotificationStatus.DELIVERED).length;
    const smsSuccess = notifs.filter((n: any) => n.preferredChannel === Channel.SMS && n.status === NotificationStatus.DELIVERED).length;
    const pushSuccess = notifs.filter((n: any) => n.preferredChannel === Channel.PUSH && n.status === NotificationStatus.DELIVERED).length;

    this.chartOption = {
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      xAxis: { type: 'category', data: ['EMAIL', 'SMS', 'PUSH'] },
      yAxis: { type: 'value' },
      series: [
        {
          data: [
            { value: emailSuccess, itemStyle: { color: '#2196f3' } },
            { value: smsSuccess, itemStyle: { color: '#4caf50' } },
            { value: pushSuccess, itemStyle: { color: '#ff9800' } }
          ],
          type: 'bar',
          barWidth: '40%'
        }
      ]
    };
  }
}
