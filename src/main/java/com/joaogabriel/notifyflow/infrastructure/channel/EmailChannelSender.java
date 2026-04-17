package com.joaogabriel.notifyflow.infrastructure.channel;

import com.joaogabriel.notifyflow.domain.enums.Channel;
import com.joaogabriel.notifyflow.domain.exception.ChannelDeliveryException;
import com.joaogabriel.notifyflow.domain.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("emailChannelSender")
public class EmailChannelSender implements ChannelSender {
    private static final Logger log = LoggerFactory.getLogger(EmailChannelSender.class);

    @Override
    public Channel getChannel() { return Channel.EMAIL; }

    @Override
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "channelSender", fallbackMethod = "fallback")
    @io.github.resilience4j.retry.annotation.Retry(name = "channelSender")
    public void send(Notification notification) {
        log.info("Sending EMAIL notification to: {} | Subject: {}",
                notification.getRecipientInfo().getEmail(),
                notification.getTemplateContent().getSubject());
    }

    public void fallback(Notification notification, Throwable t) {
        throw new ChannelDeliveryException("Fallback reached for EMAIL: " + t.getMessage(), t);
    }
}
