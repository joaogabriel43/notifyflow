package com.joaogabriel.notifyflow.presentation.controller;

import com.joaogabriel.notifyflow.application.usecase.GetNotificationUseCase;
import com.joaogabriel.notifyflow.application.usecase.SendNotificationUseCase;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for notification operations.
 * Endpoints will be implemented in Sprint 2.
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final SendNotificationUseCase sendNotificationUseCase;
    private final GetNotificationUseCase getNotificationUseCase;

    public NotificationController(SendNotificationUseCase sendNotificationUseCase,
                                  GetNotificationUseCase getNotificationUseCase) {
        this.sendNotificationUseCase = sendNotificationUseCase;
        this.getNotificationUseCase = getNotificationUseCase;
    }

    // Endpoints will be implemented in Sprint 2
}
