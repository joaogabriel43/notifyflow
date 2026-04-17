package com.joaogabriel.notifyflow.application.service;

import com.joaogabriel.notifyflow.application.dto.NotificationResponse;
import com.joaogabriel.notifyflow.application.dto.SendNotificationRequest;
import com.joaogabriel.notifyflow.application.usecase.SendNotificationUseCase;
import com.joaogabriel.notifyflow.domain.enums.NotificationStatus;
import com.joaogabriel.notifyflow.domain.model.Notification;
import com.joaogabriel.notifyflow.domain.model.NotificationTemplate;
import com.joaogabriel.notifyflow.domain.model.RecipientInfo;
import com.joaogabriel.notifyflow.domain.port.out.NotificationOutboxPort;
import com.joaogabriel.notifyflow.domain.port.out.NotificationRepositoryPort;
import com.joaogabriel.notifyflow.infrastructure.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Service implementation for sending notifications.
 * Creates the notification, persists it, and saves an outbox entry for async processing.
 */
@Service
public class SendNotificationService implements SendNotificationUseCase {

    private final NotificationRepositoryPort notificationRepository;
    private final NotificationOutboxPort notificationOutbox;
    private final NotificationMapper notificationMapper;

    public SendNotificationService(NotificationRepositoryPort notificationRepository,
                                   NotificationOutboxPort notificationOutbox,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationOutbox = notificationOutbox;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public NotificationResponse execute(SendNotificationRequest request) {
        var recipientInfo = new RecipientInfo(
                request.recipientEmail(),
                request.recipientPhone(),
                request.recipientDeviceToken()
        );

        var templateContent = new NotificationTemplate(
                request.subject(),
                request.body(),
                request.templateVariables()
        );

        var now = LocalDateTime.now();
        var notification = new Notification(
                UUID.randomUUID(),
                request.tenantId(),
                NotificationStatus.PENDING,
                request.preferredChannel(),
                request.fallbackChannels() != null ? request.fallbackChannels() : new ArrayList<>(),
                recipientInfo,
                templateContent,
                new ArrayList<>(),
                now,
                now
        );

        var saved = notificationRepository.save(notification);
        notificationOutbox.saveOutboxEntry(saved.getId(), saved.getId().toString());

        return notificationMapper.toResponse(saved);
    }
}
