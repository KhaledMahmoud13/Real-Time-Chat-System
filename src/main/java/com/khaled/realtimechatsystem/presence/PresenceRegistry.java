package com.khaled.realtimechatsystem.presence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class PresenceRegistry {
    private static final String KEY_PREFIX = "presence:user:";
    private static final Duration TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;

    public void markOnline(String username, String instanceId) {
        redisTemplate.opsForValue().set(KEY_PREFIX + username, instanceId, TTL);
    }

    public void markOffline(String username) {
        redisTemplate.delete(KEY_PREFIX + username);
    }

    public String getInstanceFor(String username) {
        return redisTemplate.opsForValue().get(KEY_PREFIX + username);
    }

    public boolean isOnline(String username) {
        return getInstanceFor(username) != null;
    }
}
