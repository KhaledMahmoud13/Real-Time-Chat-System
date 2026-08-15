package com.khaled.realtimechatsystem.messaging;

import com.khaled.realtimechatsystem.messaging.response.ChatMessageResponse;
import org.springframework.stereotype.Service;

@Service
public class MessageMapper {

    public ChatMessageResponse toResponse(Message message) {
        return new ChatMessageResponse(
                message.getConversationId(),
                message.getMessageId(),
                message.getSenderUsername(),
                message.getContent(),
                message.getStatus(),
                message.getCreatedAt()
        );
    }
}
