import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { NotificationFormComponent } from './notification-form.component';
import { NotificationSignalService } from '../../../core/services/notification-signal.service';
import { NotificationService } from '../../../core/services/notification.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('NotificationFormComponent', () => {
  let component: NotificationFormComponent;
  let fixture: ComponentFixture<NotificationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        NotificationFormComponent,
        ReactiveFormsModule,
        NoopAnimationsModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: { close: jasmine.createSpy('close') } },
        { provide: MAT_DIALOG_DATA, useValue: { tenantId: 'tenant-001' } },
        NotificationSignalService,
        NotificationService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NotificationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('form should be invalid when required fields are empty', () => {
    component.form.get('tenantId')?.setValue('');
    component.form.get('templateSubject')?.setValue('');
    component.form.get('templateBody')?.setValue('');
    expect(component.form.valid).toBeFalse();
  });

  it('tenantId field should be required', () => {
    const tenantIdCtrl = component.form.get('tenantId');
    tenantIdCtrl?.setValue('');
    expect(tenantIdCtrl?.hasError('required')).toBeTrue();
  });

  it('templateSubject field should be required', () => {
    const subjectCtrl = component.form.get('templateSubject');
    subjectCtrl?.setValue('');
    expect(subjectCtrl?.hasError('required')).toBeTrue();
  });

  it('templateBody field should be required', () => {
    const bodyCtrl = component.form.get('templateBody');
    bodyCtrl?.setValue('');
    expect(bodyCtrl?.hasError('required')).toBeTrue();
  });

  it('form should be valid when all required fields are filled', () => {
    component.form.patchValue({
      tenantId: 'tenant-001',
      recipientEmail: 'test@test.com',
      templateSubject: 'Subject',
      templateBody: 'Body content'
    });
    expect(component.form.valid).toBeTrue();
  });

  it('should close dialog on cancel', () => {
    component.onCancel();
    expect(TestBed.inject(MatDialogRef).close).toHaveBeenCalledWith(false);
  });
});
