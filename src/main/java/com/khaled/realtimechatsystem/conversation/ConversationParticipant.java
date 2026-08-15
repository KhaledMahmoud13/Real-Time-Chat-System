package com.khaled.realtimechatsystem.conversation;

import com.khaled.realtimechatsystem.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(
        name = "CONVERSATION_PARTICIPANTS",
        uniqueConstraints = @UniqueConstraint(columnNames = {"CONVERSATION_ID", "USERNAME"})
)
public class ConversationParticipant extends BaseEntity {

    @Column(name = "CONVERSATION_ID", nullable = false)
    private UUID conversationId;

    @Column(name = "USERNAME", nullable = false)
    private String username;
}
