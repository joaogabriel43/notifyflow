package com.joaogabriel.notifyflow.infrastructure.channel;

import com.joaogabriel.notifyflow.domain.model.Notification;

/**
 * Interface for channel-specific notification senders.
 * Each channel (EMAIL, SMS, PUSH) implements this interface.
 */
public interface ChannelSender {

    /**
     * Sends a notification through the specific channel.
     *
     * @param notification the notification to send
     * @return true if sent successfully, false otherwise
     */
    boolean send(Notification notification);
}
