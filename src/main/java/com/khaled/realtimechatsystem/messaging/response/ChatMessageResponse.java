package com.khaled.realtimechatsystem.messaging.response;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageResponse(
        UUID conversationId,
        UUID messageId,
        String senderUsername,
        String content,
        String status,
        Instant createdAt
) {
}
