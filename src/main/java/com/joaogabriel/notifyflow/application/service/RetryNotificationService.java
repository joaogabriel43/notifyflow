package com.joaogabriel.notifyflow.application.service;

import com.joaogabriel.notifyflow.application.dto.NotificationResponse;
import com.joaogabriel.notifyflow.domain.enums.NotificationStatus;
import com.joaogabriel.notifyflow.domain.exception.NotificationNotFoundException;
import com.joaogabriel.notifyflow.domain.model.Notification;
import com.joaogabriel.notifyflow.domain.port.out.NotificationOutboxPort;
import com.joaogabriel.notifyflow.domain.port.out.NotificationRepositoryPort;
import com.joaogabriel.notifyflow.infrastructure.mapper.NotificationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RetryNotificationService {
    private final NotificationRepositoryPort notificationRepository;
    private final NotificationOutboxPort notificationOutbox;
    private final NotificationMapper notificationMapper;

    public RetryNotificationService(NotificationRepositoryPort notificationRepository,
                                    NotificationOutboxPort notificationOutbox,
                                    NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationOutbox = notificationOutbox;
        this.notificationMapper = notificationMapper;
    }

    @Transactional
    public NotificationResponse retryNotification(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

        if (notification.getStatus() != NotificationStatus.FAILED && notification.getStatus() != NotificationStatus.EXHAUSTED) {
            throw new IllegalArgumentException("Only FAILED or EXHAUSTED notifications can be retried");
        }

        notification.resetForRetry();
        notificationRepository.save(notification);

        String payload = String.format("{\"notificationId\":\"%s\",\"tenantId\":\"%s\"}",
                notification.getId(), notification.getTenantId());
        notificationOutbox.saveOutboxEntry(notification.getId(), payload);

        return notificationMapper.toResponse(notification);
    }
}
