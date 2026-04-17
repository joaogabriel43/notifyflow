package com.joaogabriel.notifyflow.application.service;

import com.joaogabriel.notifyflow.application.dto.NotificationResponse;
import com.joaogabriel.notifyflow.application.usecase.GetNotificationUseCase;
import com.joaogabriel.notifyflow.domain.exception.NotificationNotFoundException;
import com.joaogabriel.notifyflow.domain.port.out.NotificationRepositoryPort;
import com.joaogabriel.notifyflow.infrastructure.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service implementation for retrieving notifications by ID.
 */
@Service
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
}
