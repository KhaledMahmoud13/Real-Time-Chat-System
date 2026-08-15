package com.khaled.realtimechatsystem.messaging.request;

import java.util.UUID;

public record ChatMessageRequest(
        UUID conversationId,
        String content
) {
}
