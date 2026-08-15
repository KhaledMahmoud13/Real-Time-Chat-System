package com.khaled.realtimechatsystem.conversation;

import java.util.List;
import java.util.UUID;

public interface ConversationService {

    UUID getOrCreateDirectConversation(String userA, String userB);

    List<String> getParticipants(UUID conversationId);

    boolean isParticipant(UUID conversationId, String username);
}
