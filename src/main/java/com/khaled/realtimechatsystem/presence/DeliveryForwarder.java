package com.khaled.realtimechatsystem.presence;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryForwarder {
    private static final String CHANNEL = "chat-delivery-forward";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void forwardTo(String targetInstanceId, MessageDeliveryEvent event) throws Exception {
        ForwardedDelivery forwarded = new ForwardedDelivery(targetInstanceId, event);
        redisTemplate.convertAndSend(CHANNEL, objectMapper.writeValueAsString(forwarded));
    }
}
