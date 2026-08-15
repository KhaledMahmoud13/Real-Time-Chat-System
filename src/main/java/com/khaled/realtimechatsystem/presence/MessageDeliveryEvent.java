package com.khaled.realtimechatsystem.presence;

import java.time.Instant;
import java.util.UUID;

public record MessageDeliveryEvent(
        UUID conversationId,
        UUID messageId,
        String senderUsername,
        String recipientUsername,
        String content,
        String status,
        Instant createdAt
) {
}
