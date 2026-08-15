package com.khaled.realtimechatsystem.presence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaled.realtimechatsystem.messaging.MessageService;
import com.khaled.realtimechatsystem.ws.MessageType;
import com.khaled.realtimechatsystem.ws.WebSocketSessionRegistry;
import com.khaled.realtimechatsystem.ws.outgoing.OutgoingSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageDeliveryService {

    private final PresenceService presenceService;
    private final DeliveryForwarder deliveryForwarder;
    private final WebSocketSessionRegistry registry;
    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    public void deliver(MessageDeliveryEvent event) {
        String recipient = event.recipientUsername();
        String hostingInstance = presenceService.findHostingInstance(recipient);

        if (hostingInstance == null) {
            log.info("Recipient {} is offline everywhere, message {} stays pending", recipient, event.messageId());
            return;
        }

        if (presenceService.isCurrentInstance(recipient)) {
            deliverLocally(event);
        } else {
            try {
                deliveryForwarder.forwardTo(hostingInstance, event);
                log.info("Forwarded message {} to instance {} for {}", event.messageId(), hostingInstance, recipient);
            } catch (Exception e) {
                log.error("Failed to forward message {} to instance {}", event.messageId(), hostingInstance, e);
            }
        }
    }

    private void deliverLocally(MessageDeliveryEvent event) {
        WebSocketSession session = registry.get(event.recipientUsername());

        if (session == null || !session.isOpen()) {
            log.warn("Presence said {} is on this instance, but no local session found", event.recipientUsername());
            return;
        }

        try {
            OutgoingSocketMessage outgoing = OutgoingSocketMessage.of(MessageType.CHAT_MESSAGE, event);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(outgoing)));
//            messageService.updateStatus(event.conversationId(), event.messageId(), MessageStatus.DELIVERED.name());
            log.info("Delivered message {} to {}", event.messageId(), event.recipientUsername());
        } catch (Exception e) {
            log.error("Failed to deliver message {} to {}", event.messageId(), event.recipientUsername(), e);
        }
    }
}
