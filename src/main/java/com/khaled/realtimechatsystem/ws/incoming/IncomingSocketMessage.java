package com.khaled.realtimechatsystem.ws.incoming;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.khaled.realtimechatsystem.ws.MessageType;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IncomingSocketMessage(
        MessageType type,
        UUID conversationId,
        String content,
        String clientMessageId,
        UUID messageId
) {
}
