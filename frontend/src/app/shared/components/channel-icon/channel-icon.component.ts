import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { Channel } from '../../../core/models/notification.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-channel-icon',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './channel-icon.component.html',
  styleUrl: './channel-icon.component.scss'
})
export class ChannelIconComponent {
  @Input({ required: true }) channel!: Channel | string;

  get icon(): string {
    switch (this.channel) {
      case Channel.EMAIL: return 'mail';
      case Channel.SMS: return 'sms';
      case Channel.PUSH: return 'notifications';
      default: return 'info';
    }
  }

  get colorClass(): string {
    switch (this.channel) {
      case Channel.EMAIL: return 'icon-email';
      case Channel.SMS: return 'icon-sms';
      case Channel.PUSH: return 'icon-push';
      default: return 'icon-default';
    }
  }
}
