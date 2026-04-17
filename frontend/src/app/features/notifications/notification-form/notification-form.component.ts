import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { NotificationSignalService } from '../../../core/services/notification-signal.service';
import { Channel } from '../../../core/models/notification.model';

@Component({
  selector: 'app-notification-form',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule, MatButtonModule,
    MatFormFieldModule, MatInputModule, MatSelectModule
  ],
  templateUrl: './notification-form.component.html',
  styleUrl: './notification-form.component.scss'
})
export class NotificationFormComponent implements OnInit {
  form!: FormGroup;
  channels = Object.values(Channel);

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<NotificationFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { tenantId: string },
    private notificationState: NotificationSignalService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      tenantId: [this.data?.tenantId || '', Validators.required],
      preferredChannel: [Channel.EMAIL, Validators.required],
      fallbackChannels: [[]],
      recipientEmail: [''],
      recipientPhone: [''],
      recipientDeviceToken: [''],
      templateSubject: ['', Validators.required],
      templateBody: ['', Validators.required]
    });

    // Add conditional validators
    this.form.get('preferredChannel')?.valueChanges.subscribe(channel => {
      const emailCtrl = this.form.get('recipientEmail');
      const phoneCtrl = this.form.get('recipientPhone');
      const pushCtrl = this.form.get('recipientDeviceToken');

      emailCtrl?.clearValidators();
      phoneCtrl?.clearValidators();
      pushCtrl?.clearValidators();

      if (channel === Channel.EMAIL) {
        emailCtrl?.setValidators([Validators.required, Validators.email]);
      } else if (channel === Channel.SMS) {
        phoneCtrl?.setValidators([Validators.required]);
      } else if (channel === Channel.PUSH) {
        pushCtrl?.setValidators([Validators.required]);
      }

      emailCtrl?.updateValueAndValidity();
      phoneCtrl?.updateValueAndValidity();
      pushCtrl?.updateValueAndValidity();
    });

    // Trigger validation for initial EMAIL
    this.form.get('preferredChannel')?.setValue(Channel.EMAIL);
  }

  onSubmit(): void {
    if (this.form.valid) {
      const formValue = this.form.value;
      this.notificationState.sendNotification(formValue);
      // Let the caller handle the snackbar based on the state later, or we can just close on submit
      this.dialogRef.close(true);
    } else {
      this.form.markAllAsTouched();
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
