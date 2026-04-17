package com.joaogabriel.notifyflow.infrastructure.messaging.consumer;

import com.joaogabriel.notifyflow.infrastructure.messaging.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer for processing notification messages from RabbitMQ.
 * Actual processing logic will be implemented in Sprint 2.
 */
@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void consume(String message) {
        log.info("Received notification message: {}", message);
        // Processing logic will be implemented in Sprint 2
    }
}
