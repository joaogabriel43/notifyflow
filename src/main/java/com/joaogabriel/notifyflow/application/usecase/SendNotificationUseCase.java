package com.joaogabriel.notifyflow.application.usecase;

import com.joaogabriel.notifyflow.application.dto.NotificationResponse;
import com.joaogabriel.notifyflow.application.dto.SendNotificationRequest;

/**
 * Use case interface for sending notifications.
 */
public interface SendNotificationUseCase {

    NotificationResponse execute(SendNotificationRequest request);
}
