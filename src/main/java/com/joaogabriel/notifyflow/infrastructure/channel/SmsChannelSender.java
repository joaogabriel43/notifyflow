package com.joaogabriel.notifyflow.infrastructure.channel;

import com.joaogabriel.notifyflow.domain.enums.Channel;
import com.joaogabriel.notifyflow.domain.exception.ChannelDeliveryException;
import com.joaogabriel.notifyflow.domain.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component("smsChannelSender")
public class SmsChannelSender implements ChannelSender {
    private static final Logger log = LoggerFactory.getLogger(SmsChannelSender.class);
    private final Random random = new Random();

    @Override
    public Channel getChannel() { return Channel.SMS; }

    @Override
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "channelSender", fallbackMethod = "fallback")
    @io.github.resilience4j.retry.annotation.Retry(name = "channelSender")
    public void send(Notification notification) {
        log.info("Sending SMS notification to: {} | Body: {}",
                notification.getRecipientInfo().getPhoneNumber(),
                notification.getTemplateContent().getBody());
        if (random.nextInt(100) < 30) {
            throw new ChannelDeliveryException("Simulated instability in SMS delivery");
        }
    }

    public void fallback(Notification notification, Throwable t) {
        throw new ChannelDeliveryException("Fallback reached for SMS: " + t.getMessage(), t);
    }
}
