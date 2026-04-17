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
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Service implementation for sending notifications.
 * Creates the notification, persists it, and saves an outbox entry for async processing.
 * Everything runs within a single @Transactional to guarantee consistency (Outbox Pattern).
 */
import com.joaogabriel.notifyflow.infrastructure.ratelimit.TenantRateLimiterService;
import com.joaogabriel.notifyflow.domain.exception.RateLimitExceededException;

@Service
public class SendNotificationService implements SendNotificationUseCase {

    private static final Logger log = LoggerFactory.getLogger(SendNotificationService.class);

    private final NotificationRepositoryPort notificationRepository;
    private final NotificationOutboxPort notificationOutbox;
    private final NotificationMapper notificationMapper;
    private final TenantRateLimiterService tenantRateLimiterService;

    public SendNotificationService(NotificationRepositoryPort notificationRepository,
                                   NotificationOutboxPort notificationOutbox,
                                   NotificationMapper notificationMapper,
                                   TenantRateLimiterService tenantRateLimiterService) {
        this.notificationRepository = notificationRepository;
        this.notificationOutbox = notificationOutbox;
        this.notificationMapper = notificationMapper;
        this.tenantRateLimiterService = tenantRateLimiterService;
    }

    @Override
    @Transactional
    public NotificationResponse execute(SendNotificationRequest request) {
        log.info("Processing notification for tenant: {}", request.tenantId());
        try {
            if (!tenantRateLimiterService.acquirePermission(request.tenantId())) {
                throw new RateLimitExceededException(request.tenantId());
            }
            var recipientInfo = new RecipientInfo(
                    request.recipientEmail(),
                    request.recipientPhone(),
                    request.recipientDeviceToken()
            );

            var templateContent = new NotificationTemplate(
                    request.templateSubject(),
                    request.templateBody(),
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

            String payload = String.format("{\"notificationId\":\"%s\",\"tenantId\":\"%s\"}",
                    saved.getId(), saved.getTenantId());
            notificationOutbox.saveOutboxEntry(saved.getId(), payload);

            log.info("Notification saved successfully with id: {}", saved.getId());
            return notificationMapper.toResponse(saved);
        } catch (Exception e) {
            log.error("Failed to save notification: {}", e.getMessage(), e);
            throw e;
        }
    }
}
