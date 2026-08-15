package com.khaled.realtimechatsystem.messaging.response;

import java.util.List;
import java.util.UUID;

public record ConversationHistoryPage(
        List<ChatMessageResponse> messages,
        UUID nextCursor,
        boolean hasMore
) {
}
