package com.khaled.realtimechatsystem.messaging;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends CassandraRepository<Message, UUID> {

    @Query("SELECT * FROM messages_by_conversation WHERE conversation_id = ?0 LIMIT ?1")
    List<Message> findRecentMessages(UUID conversationId, int limit);

    @Query("SELECT * FROM messages_by_conversation WHERE conversation_id = ?0 AND message_id < ?1 LIMIT ?2")
    List<Message> findMessagesBefore(UUID conversationId, UUID beforeMessageId, int limit);

    @Query("UPDATE messages_by_conversation SET status = ?2 WHERE conversation_id = ?0 AND message_id = ?1")
    void updateStatus(UUID conversationId, UUID messageId, String status);
}
