package com.joaogabriel.notifyflow.application.usecase;

import com.joaogabriel.notifyflow.application.dto.NotificationResponse;

import java.util.UUID;

/**
 * Use case interface for retrieving notifications.
 */
public interface GetNotificationUseCase {

    NotificationResponse execute(UUID id);
}
