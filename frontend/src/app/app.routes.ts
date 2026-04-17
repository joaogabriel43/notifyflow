import { Routes } from '@angular/router';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { NotificationListComponent } from './features/notifications/notification-list/notification-list.component';
import { NotificationDetailComponent } from './features/notifications/notification-detail/notification-detail.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'notifications', component: NotificationListComponent },
  { path: 'notifications/:id', component: NotificationDetailComponent }
];
