package com.khaled.realtimechatsystem.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaled.realtimechatsystem.conversation.ConversationService;
import com.khaled.realtimechatsystem.messaging.MessageService;
import com.khaled.realtimechatsystem.messaging.MessageStatus;
import com.khaled.realtimechatsystem.messaging.response.ChatMessageResponse;
import com.khaled.realtimechatsystem.presence.MessageDeliveryEvent;
import com.khaled.realtimechatsystem.presence.MessageDeliveryService;
import com.khaled.realtimechatsystem.presence.PresenceService;
import com.khaled.realtimechatsystem.ws.incoming.IncomingSocketMessage;
import com.khaled.realtimechatsystem.ws.outgoing.OutgoingSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionRegistry registry;
    private final PresenceService presenceService;
    private final MessageService messageService;
    private final ConversationService conversationService;
    private final MessageDeliveryService messageDeliveryService;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");
        registry.register(username, session);
        presenceService.userConnected(username);
        log.info("User {} connected, session {}", username, session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String username = (String) session.getAttributes().get("username");

        try {
            IncomingSocketMessage incoming = objectMapper.readValue(message.getPayload(), IncomingSocketMessage.class);
            dispatch(session, username, incoming);
        } catch (Exception e) {
            log.warn("Failed to process message from {}: {}", username, e.getMessage());
            sendError(session, "Malformed message");
        }
    }

    private void dispatch(WebSocketSession session, String username, IncomingSocketMessage incoming) throws IOException {
        if (incoming.type() == null) {
            sendError(session, "Missing message type");
            return;
        }

        switch (incoming.type()) {
            case CHAT_MESSAGE -> handleChatMessage(session, username, incoming);
            case TYPING -> handleTyping(session, username, incoming);
            case READ_RECEIPT -> handleReadReceipt(session, username, incoming);
            default -> sendError(session, "Unsupported message type: " + incoming.type());
        }
    }

    private void handleChatMessage(WebSocketSession session, String username, IncomingSocketMessage incoming) throws IOException {
        if (incoming.conversationId() == null || incoming.content() == null || incoming.content().isBlank()) {
            sendError(session, "conversationId and content are required for CHAT_MESSAGE");
            return;
        }

        if (!conversationService.isParticipant(incoming.conversationId(), username)) {
            sendError(session, "You are not a participant in this conversation");
            return;
        }

        ChatMessageResponse saved = messageService.saveMessage(
                incoming.conversationId(),
                username,
                incoming.content()
        );

        List<String> recipients = conversationService.getParticipants(incoming.conversationId())
                .stream()
                .filter(participant -> !participant.equals(username))
                .toList();

        for (String recipient : recipients) {
            MessageDeliveryEvent event = new MessageDeliveryEvent(
                    saved.conversationId(),
                    saved.messageId(),
                    saved.senderUsername(),
                    recipient,
                    saved.content(),
                    saved.status(),
                    saved.createdAt()
            );
            messageDeliveryService.deliver(event);
        }

        send(session, OutgoingSocketMessage.of(MessageType.CHAT_MESSAGE, saved));
    }

    private void handleTyping(WebSocketSession session, String username, IncomingSocketMessage incoming) {
        log.info("{} is typing in conversation {}", username, incoming.conversationId());
    }

    private void handleReadReceipt(WebSocketSession session, String username, IncomingSocketMessage incoming) {
        if (incoming.conversationId() == null || incoming.messageId() == null) {
            sendError(session, "conversationId and messageId are required for READ_RECEIPT");
            return;
        }
        messageService.updateStatus(incoming.conversationId(), incoming.messageId(), MessageStatus.READ.name());
        log.info("{} marked message {} as read", username, incoming.messageId());
    }

    private void send(WebSocketSession session, OutgoingSocketMessage message) throws IOException {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
    }

    private void sendError(WebSocketSession session, String errorMessage) {
        try {
            send(session, OutgoingSocketMessage.of(MessageType.ERROR, new ErrorPayload(errorMessage)));
        } catch (IOException e) {
            log.error("Failed to send error message to session {}", session.getId(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String username = (String) session.getAttributes().get("username");
        registry.remove(username);
        presenceService.userDisconnected(username);
        log.info("User {} disconnected: {}", username, status);
    }
}
