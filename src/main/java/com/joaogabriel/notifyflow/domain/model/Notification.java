package com.joaogabriel.notifyflow.domain.model;

import com.joaogabriel.notifyflow.domain.enums.AttemptResult;
import com.joaogabriel.notifyflow.domain.enums.Channel;
import com.joaogabriel.notifyflow.domain.enums.NotificationStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Aggregate Root representing a notification in the system.
 * Contains all domain logic for channel fallback, attempt tracking, and status transitions.
 */
public class Notification {

    private final UUID id;
    private final String tenantId;
    private NotificationStatus status;
    private final Channel preferredChannel;
    private final List<Channel> fallbackChannels;
    private final RecipientInfo recipientInfo;
    private final NotificationTemplate templateContent;
    private final List<DeliveryAttempt> attempts;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Notification(UUID id, String tenantId, NotificationStatus status, Channel preferredChannel,
                        List<Channel> fallbackChannels, RecipientInfo recipientInfo,
                        NotificationTemplate templateContent, List<DeliveryAttempt> attempts,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.status = status;
        this.preferredChannel = preferredChannel;
        this.fallbackChannels = fallbackChannels != null ? new ArrayList<>(fallbackChannels) : new ArrayList<>();
        this.recipientInfo = recipientInfo;
        this.templateContent = templateContent;
        this.attempts = attempts != null ? new ArrayList<>(attempts) : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Registers a new delivery attempt for this notification.
     */
    public void registerAttempt(DeliveryAttempt attempt) {
        this.attempts.add(attempt);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks the notification as successfully delivered.
     */
    public void markDelivered() {
        this.status = NotificationStatus.DELIVERED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Resets the notification status to PENDING for retry.
     */
    public void resetForRetry() {
        this.status = NotificationStatus.PENDING;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks the notification as failed.
     */
    public void markFailed() {
        this.status = NotificationStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks the notification as exhausted — all channels have been tried without success.
     */
    public void exhaustAllChannels() {
        this.status = NotificationStatus.EXHAUSTED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Returns the next available channel to attempt delivery.
     * First tries the preferred channel, then falls back through the fallback list,
     * skipping any channels that have already been attempted with failure.
     *
     * @return Optional containing the next channel to try, or empty if all exhausted
     */
    public Optional<Channel> getNextChannel() {
        Set<Channel> failedChannels = attempts.stream()
                .filter(a -> a.getResult() == AttemptResult.FAILED)
                .map(DeliveryAttempt::getChannel)
                .collect(Collectors.toSet());

        if (!failedChannels.contains(preferredChannel)) {
            return Optional.of(preferredChannel);
        }

        return fallbackChannels.stream()
                .filter(channel -> !failedChannels.contains(channel))
                .findFirst();
    }

    /**
     * Checks if there are still untried channels available for fallback.
     *
     * @return true if there are remaining channels to try
     */
    public boolean hasMoreChannels() {
        return getNextChannel().isPresent();
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public Channel getPreferredChannel() {
        return preferredChannel;
    }

    public List<Channel> getFallbackChannels() {
        return List.copyOf(fallbackChannels);
    }

    public RecipientInfo getRecipientInfo() {
        return recipientInfo;
    }

    public NotificationTemplate getTemplateContent() {
        return templateContent;
    }

    public List<DeliveryAttempt> getAttempts() {
        return List.copyOf(attempts);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
