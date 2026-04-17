package com.joaogabriel.notifyflow.infrastructure.channel;

import com.joaogabriel.notifyflow.domain.enums.Channel;
import com.joaogabriel.notifyflow.domain.exception.ChannelDeliveryException;
import com.joaogabriel.notifyflow.domain.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("pushChannelSender")
public class PushChannelSender implements ChannelSender {
    private static final Logger log = LoggerFactory.getLogger(PushChannelSender.class);

    @Override
    public Channel getChannel() { return Channel.PUSH; }

    @Override
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "channelSender", fallbackMethod = "fallback")
    @io.github.resilience4j.retry.annotation.Retry(name = "channelSender")
    public void send(Notification notification) {
        log.info("Sending PUSH notification to device: {} | Body: {}",
                notification.getRecipientInfo().getDeviceToken(),
                notification.getTemplateContent().getBody());
    }

    public void fallback(Notification notification, Throwable t) {
        throw new ChannelDeliveryException("Fallback reached for PUSH: " + t.getMessage(), t);
    }
}
