package com.khaled.realtimechatsystem.conversation;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "CONVERSATIONS")
public class Conversation {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ConversationType type;

    private Instant createdAt;
}
