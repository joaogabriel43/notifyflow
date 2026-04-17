import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChannelIconComponent } from './channel-icon.component';

describe('ChannelIconComponent', () => {
  let component: ChannelIconComponent;
  let fixture: ComponentFixture<ChannelIconComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChannelIconComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ChannelIconComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
