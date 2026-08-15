package com.khaled.realtimechatsystem.presence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaled.realtimechatsystem.ws.MessageType;
import com.khaled.realtimechatsystem.ws.WebSocketSessionRegistry;
import com.khaled.realtimechatsystem.ws.outgoing.OutgoingSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryForwardListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final WebSocketSessionRegistry registry;
    private final InstanceIdProvider instanceIdProvider;

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        try {
            ForwardedDelivery forwarded = objectMapper.readValue(message.getBody(), ForwardedDelivery.class);

            if (!instanceIdProvider.getInstanceId().equals(forwarded.targetInstanceId())) {
                return;
            }

            MessageDeliveryEvent event = forwarded.event();
            WebSocketSession session = registry.get(event.recipientUsername());

            if (session != null && session.isOpen()) {
                OutgoingSocketMessage outgoing = OutgoingSocketMessage.of(MessageType.CHAT_MESSAGE, event);
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(outgoing)));
                log.info("Cross-node delivered message {} to {}", event.messageId(), event.recipientUsername());
            }

        } catch (Exception e) {
            log.error("Failed to process forwarded delivery", e);
        }
    }
}
