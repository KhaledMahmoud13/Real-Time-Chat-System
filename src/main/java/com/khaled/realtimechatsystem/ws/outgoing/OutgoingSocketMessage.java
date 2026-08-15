package com.khaled.realtimechatsystem.ws.outgoing;

import com.khaled.realtimechatsystem.ws.MessageType;

public record OutgoingSocketMessage(
        MessageType type,
        Object payload
) {
    public static OutgoingSocketMessage of(MessageType type, Object payload) {
        return new OutgoingSocketMessage(type, payload);
    }
}