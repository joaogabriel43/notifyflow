package com.joaogabriel.notifyflow.infrastructure.mapper;

import com.joaogabriel.notifyflow.application.dto.DeliveryAttemptResponse;
import com.joaogabriel.notifyflow.application.dto.NotificationResponse;
import com.joaogabriel.notifyflow.domain.enums.Channel;
import com.joaogabriel.notifyflow.domain.model.DeliveryAttempt;
import com.joaogabriel.notifyflow.domain.model.Notification;
import com.joaogabriel.notifyflow.domain.model.NotificationTemplate;
import com.joaogabriel.notifyflow.domain.model.RecipientInfo;
import com.joaogabriel.notifyflow.infrastructure.persistence.entity.DeliveryAttemptEntity;
import com.joaogabriel.notifyflow.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper component for converting between domain models, JPA entities, and DTOs.
 * Uses manual mapping instead of MapStruct generated code for Sprint 1 simplicity.
 * Will be migrated to full MapStruct interface in subsequent sprints.
 */
@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        List<DeliveryAttemptResponse> attemptResponses = notification.getAttempts().stream()
                .map(this::toAttemptResponse)
                .collect(Collectors.toList());

        return new NotificationResponse(
                notification.getId(),
                notification.getTenantId(),
                notification.getStatus(),
                notification.getPreferredChannel(),
                notification.getFallbackChannels(),
                notification.getRecipientInfo().getEmail(),
                notification.getRecipientInfo().getPhoneNumber(),
                notification.getRecipientInfo().getDeviceToken(),
                notification.getTemplateContent().getSubject(),
                notification.getTemplateContent().getBody(),
                attemptResponses,
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }

    public DeliveryAttemptResponse toAttemptResponse(DeliveryAttempt attempt) {
        return new DeliveryAttemptResponse(
                attempt.getId(),
                attempt.getChannel(),
                attempt.getResult(),
                attempt.getErrorMessage(),
                attempt.getAttemptedAt()
        );
    }

    public NotificationEntity toEntity(Notification notification) {
        var entity = NotificationEntity.builder()
                .id(notification.getId())
                .tenantId(notification.getTenantId())
                .status(notification.getStatus())
                .preferredChannel(notification.getPreferredChannel())
                .fallbackChannels(channelListToString(notification.getFallbackChannels()))
                .recipientEmail(notification.getRecipientInfo().getEmail())
                .recipientPhone(notification.getRecipientInfo().getPhoneNumber())
                .recipientDeviceToken(notification.getRecipientInfo().getDeviceToken())
                .templateSubject(notification.getTemplateContent().getSubject())
                .templateBody(notification.getTemplateContent().getBody())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();

        if (notification.getAttempts() != null) {
            List<DeliveryAttemptEntity> attemptEntities = notification.getAttempts().stream()
                    .map(attempt -> toAttemptEntity(attempt, entity))
                    .collect(Collectors.toList());
            entity.setAttempts(attemptEntities);
        }

        return entity;
    }

    public Notification toDomain(NotificationEntity entity) {
        var recipientInfo = new RecipientInfo(
                entity.getRecipientEmail(),
                entity.getRecipientPhone(),
                entity.getRecipientDeviceToken()
        );

        var templateContent = new NotificationTemplate(
                entity.getTemplateSubject(),
                entity.getTemplateBody(),
                null
        );

        List<DeliveryAttempt> attempts = Collections.emptyList();
        if (entity.getAttempts() != null) {
            attempts = entity.getAttempts().stream()
                    .map(this::toAttemptDomain)
                    .collect(Collectors.toList());
        }

        return new Notification(
                entity.getId(),
                entity.getTenantId(),
                entity.getStatus(),
                entity.getPreferredChannel(),
                stringToChannelList(entity.getFallbackChannels()),
                recipientInfo,
                templateContent,
                attempts,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private DeliveryAttemptEntity toAttemptEntity(DeliveryAttempt attempt, NotificationEntity notification) {
        return DeliveryAttemptEntity.builder()
                .id(attempt.getId())
                .notification(notification)
                .channel(attempt.getChannel())
                .result(attempt.getResult())
                .errorMessage(attempt.getErrorMessage())
                .attemptedAt(attempt.getAttemptedAt())
                .build();
    }

    private DeliveryAttempt toAttemptDomain(DeliveryAttemptEntity entity) {
        return new DeliveryAttempt(
                entity.getId(),
                entity.getChannel(),
                entity.getResult(),
                entity.getErrorMessage(),
                entity.getAttemptedAt()
        );
    }

    private String channelListToString(List<Channel> channels) {
        if (channels == null || channels.isEmpty()) return null;
        return channels.stream()
                .map(Channel::name)
                .collect(Collectors.joining(","));
    }

    private List<Channel> stringToChannelList(String channels) {
        if (channels == null || channels.isBlank()) return Collections.emptyList();
        return Arrays.stream(channels.split(","))
                .map(String::trim)
                .map(Channel::valueOf)
                .collect(Collectors.toList());
    }
}
