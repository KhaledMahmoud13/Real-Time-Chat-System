package com.khaled.realtimechatsystem.conversation.impl;

import com.khaled.realtimechatsystem.conversation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;

    @Override
    @Transactional
    public UUID getOrCreateDirectConversation(String userA, String userB) {
        UUID conversationId = deriveDirectConversationId(userA, userB);

        if (conversationRepository.existsById(conversationId)) {
            return conversationId;
        }

        Conversation conversation = Conversation.builder()
                .id(conversationId)
                .type(ConversationType.DIRECT)
                .createdAt(Instant.now())
                .build();
        conversationRepository.save(conversation);

        participantRepository.save(ConversationParticipant.builder()
                .conversationId(conversationId)
                .username(userA)
                .build());

        participantRepository.save(ConversationParticipant.builder()
                .conversationId(conversationId)
                .username(userB)
                .build());

        return conversationId;
    }

    @Override
    public List<String> getParticipants(UUID conversationId) {
        return participantRepository.findByConversationId(conversationId)
                .stream()
                .map(ConversationParticipant::getUsername)
                .toList();
    }

    @Override
    public boolean isParticipant(UUID conversationId, String username) {
        return participantRepository.existsByConversationIdAndUsername(conversationId, username);
    }

    private UUID deriveDirectConversationId(String userA, String userB) {
        String[] sorted = Stream.of(userA, userB).sorted().toArray(String[]::new);
        String key = sorted[0] + ":" + sorted[1];
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }
}
