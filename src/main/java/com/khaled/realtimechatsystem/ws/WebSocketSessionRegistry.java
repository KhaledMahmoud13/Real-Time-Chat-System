package com.khaled.realtimechatsystem.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionRegistry {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void register(String username, WebSocketSession session) {
        sessions.put(username, session);
    }

    public void remove(String username) {
        sessions.remove(username);
    }

    public WebSocketSession get(String username) {
        return sessions.get(username);
    }

    public boolean isOnline(String username) {
        WebSocketSession session = sessions.get(username);
        return session != null && session.isOpen();
    }
}
