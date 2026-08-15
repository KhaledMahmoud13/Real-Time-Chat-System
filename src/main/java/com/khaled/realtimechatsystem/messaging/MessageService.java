package com.khaled.realtimechatsystem.messaging;

import com.khaled.realtimechatsystem.messaging.response.ChatMessageResponse;
import com.khaled.realtimechatsystem.messaging.response.ConversationHistoryPage;

import java.util.UUID;

public interface MessageService {

    ChatMessageResponse saveMessage(UUID conversationId, String senderUsername, String content);

    ConversationHistoryPage getConversationHistory(UUID conversationId, int limit);

    ConversationHistoryPage getConversationHistoryBefore(UUID conversationId, UUID beforeMessageId, int limit);

    void updateStatus(UUID conversationId, UUID messageId, String status);
}
