package com.joaogabriel.notifyflow.application.usecase;

import com.joaogabriel.notifyflow.application.dto.DeliveryAttemptResponse;
import com.joaogabriel.notifyflow.application.dto.NotificationResponse;
import com.joaogabriel.notifyflow.domain.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Use case interface for retrieving notifications.
 */
public interface GetNotificationUseCase {

    NotificationResponse execute(UUID id);

    Page<NotificationResponse> findByTenantId(String tenantId, NotificationStatus status, Pageable pageable);

    List<DeliveryAttemptResponse> findAttemptsByNotificationId(UUID id);
}
