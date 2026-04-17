package com.joaogabriel.notifyflow.infrastructure.messaging.consumer;

import com.joaogabriel.notifyflow.domain.enums.Channel;
import com.joaogabriel.notifyflow.domain.model.Notification;
import com.joaogabriel.notifyflow.domain.port.out.NotificationRepositoryPort;
import com.joaogabriel.notifyflow.infrastructure.channel.NotificationDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NotificationConsumer {
    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationRepositoryPort notificationRepository;
    private final NotificationDispatcher dispatcher;

    public NotificationConsumer(NotificationRepositoryPort notificationRepository, NotificationDispatcher dispatcher) {
        this.notificationRepository = notificationRepository;
        this.dispatcher = dispatcher;
    }

    @RabbitListener(queues = "notifyflow.queue.send")
    @Transactional
    public void consume(NotificationMessage message) {
        log.info("Received notification message for ID: {}", message.notificationId());

        Notification notification = notificationRepository.findById(message.notificationId())
                .orElseThrow(() -> new IllegalArgumentException("Notification not found for id: " + message.notificationId()));

        Channel targetChannel = message.channel() != null ? message.channel() : notification.getPreferredChannel();

        boolean delivered = false;

        while (true) {
            boolean success = dispatcher.dispatch(notification, targetChannel);

            if (success) {
                notification.markDelivered();
                notificationRepository.save(notification);
                log.info("Notification {} delivered via {}", notification.getId(), targetChannel);
                delivered = true;
                break;
            } else {
                if (notification.hasMoreChannels()) {
                    Channel nextChannel = notification.getNextChannel().orElseThrow();
                    log.info("Notification {} failed on {}, falling back to {}", notification.getId(), targetChannel, nextChannel);
                    targetChannel = nextChannel;
                } else {
                    notification.exhaustAllChannels();
                    notificationRepository.save(notification);
                    log.warn("Notification {} exhausted all channels", notification.getId());
                    break;
                }
            }
        }
    }
}
