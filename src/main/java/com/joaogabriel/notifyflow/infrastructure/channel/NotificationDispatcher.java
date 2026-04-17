package com.joaogabriel.notifyflow.infrastructure.channel;

import com.joaogabriel.notifyflow.domain.enums.AttemptResult;
import com.joaogabriel.notifyflow.domain.enums.Channel;
import com.joaogabriel.notifyflow.domain.exception.ChannelDeliveryException;
import com.joaogabriel.notifyflow.domain.model.DeliveryAttempt;
import com.joaogabriel.notifyflow.domain.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificationDispatcher {
    private static final Logger log = LoggerFactory.getLogger(NotificationDispatcher.class);
    private final Map<Channel, ChannelSender> senders;

    public NotificationDispatcher(List<ChannelSender> senderList) {
        this.senders = senderList.stream()
                .collect(Collectors.toMap(ChannelSender::getChannel, Function.identity()));
    }

    public boolean dispatch(Notification notification, Channel channel) {
        ChannelSender sender = senders.get(channel);
        if (sender == null) {
            log.error("No ChannelSender configured for channel: {}", channel);
            DeliveryAttempt attempt = new DeliveryAttempt(
                    UUID.randomUUID(), channel, AttemptResult.FAILED,
                    "No sender configured for channel " + channel, LocalDateTime.now()
            );
            notification.registerAttempt(attempt);
            return false;
        }

        try {
            sender.send(notification);
            DeliveryAttempt attempt = new DeliveryAttempt(
                    UUID.randomUUID(), channel, AttemptResult.SUCCESS,
                    "Delivered successfully", LocalDateTime.now()
            );
            notification.registerAttempt(attempt);
            return true;
        } catch (ChannelDeliveryException e) {
            log.warn("Failed to deliver notification {} via {}: {}", notification.getId(), channel, e.getMessage());
            DeliveryAttempt attempt = new DeliveryAttempt(
                    UUID.randomUUID(), channel, AttemptResult.FAILED,
                    e.getMessage(), LocalDateTime.now()
            );
            notification.registerAttempt(attempt);
            return false;
        }
    }
}
