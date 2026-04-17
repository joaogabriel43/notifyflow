package com.joaogabriel.notifyflow.application.service;

import com.joaogabriel.notifyflow.application.dto.DeliveryAttemptResponse;
import com.joaogabriel.notifyflow.application.dto.NotificationResponse;
import com.joaogabriel.notifyflow.application.usecase.GetNotificationUseCase;
import com.joaogabriel.notifyflow.domain.enums.NotificationStatus;
import com.joaogabriel.notifyflow.domain.exception.NotificationNotFoundException;
import com.joaogabriel.notifyflow.domain.port.out.NotificationRepositoryPort;
import com.joaogabriel.notifyflow.infrastructure.mapper.NotificationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for retrieving notifications.
 */
@Service
@Transactional(readOnly = true)
public class GetNotificationService implements GetNotificationUseCase {

    private final NotificationRepositoryPort notificationRepository;
    private final NotificationMapper notificationMapper;

    public GetNotificationService(NotificationRepositoryPort notificationRepository,
                                  NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public NotificationResponse execute(UUID id) {
        var notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        return notificationMapper.toResponse(notification);
    }

    @Override
    public Page<NotificationResponse> findByTenantId(String tenantId, NotificationStatus status, Pageable pageable) {
        return notificationRepository.findByTenantIdAndStatus(tenantId, status, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    public Page<NotificationResponse> findAll(NotificationStatus status, Pageable pageable) {
        return notificationRepository.findAll(status, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    public List<DeliveryAttemptResponse> findAttemptsByNotificationId(UUID id) {
        var notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        return notification.getAttempts().stream()
                .map(notificationMapper::toAttemptResponse)
                .collect(Collectors.toList());
    }
}
