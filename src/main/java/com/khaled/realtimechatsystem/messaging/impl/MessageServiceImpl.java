package com.khaled.realtimechatsystem.messaging.impl;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.khaled.realtimechatsystem.messaging.*;
import com.khaled.realtimechatsystem.messaging.response.ChatMessageResponse;
import com.khaled.realtimechatsystem.messaging.response.ConversationHistoryPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    public ChatMessageResponse saveMessage(UUID conversationId, String senderUsername, String content) {
        Message message = Message.builder()
                .conversationId(conversationId)
                .messageId(Uuids.timeBased())
                .senderUsername(senderUsername)
                .content(content)
                .status(MessageStatus.SENT.name())
                .createdAt(Instant.now())
                .build();

        Message saved = messageRepository.save(message);
        return messageMapper.toResponse(saved);
    }


    @Override
    public ConversationHistoryPage getConversationHistory(UUID conversationId, int limit) {
        List<Message> messages = messageRepository.findRecentMessages(conversationId, limit + 1);
        return buildPage(messages, limit);
    }

    @Override
    public ConversationHistoryPage getConversationHistoryBefore(UUID conversationId, UUID beforeMessageId, int limit) {
        List<Message> messages = messageRepository.findMessagesBefore(conversationId, beforeMessageId, limit + 1);
        return buildPage(messages, limit);
    }

    @Override
    public void updateStatus(UUID conversationId, UUID messageId, String status) {
        messageRepository.updateStatus(conversationId, messageId, status);
        log.debug("Updated message {} status to {}", messageId, status);
    }

    private ConversationHistoryPage buildPage(List<Message> messages, int limit) {
        boolean hasMore = messages.size() > limit;

        List<ChatMessageResponse> page = messages.stream()
                .limit(limit)
                .map(messageMapper::toResponse)
                .toList();

        UUID nextCursor = page.isEmpty() ? null : page.get(page.size() - 1).messageId();

        return new ConversationHistoryPage(page, nextCursor, hasMore);
    }
}
