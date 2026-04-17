package com.joaogabriel.notifyflow.infrastructure.messaging.consumer;

import com.joaogabriel.notifyflow.domain.port.out.NotificationRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeadLetterConsumer {
    private static final Logger log = LoggerFactory.getLogger(DeadLetterConsumer.class);
    private final NotificationRepositoryPort notificationRepository;

    public DeadLetterConsumer(NotificationRepositoryPort notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @RabbitListener(queues = "notifyflow.queue.send.dlq")
    @Transactional
    public void consume(NotificationMessage message, Message amqpMessage) {
        log.error("Message sent to DLQ for notification: {} - Headers: {}", message.notificationId(), amqpMessage.getMessageProperties().getHeaders());
        
        notificationRepository.findById(message.notificationId()).ifPresent(notification -> {
            notification.markFailed();
            notificationRepository.save(notification);
            log.info("Notification {} marked as FAILED in database after reaching DLQ", notification.getId());
        });
    }
}
