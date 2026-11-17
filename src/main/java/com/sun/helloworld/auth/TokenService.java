package com.sun.helloworld.auth;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenService {

    private static final long TTL_SECONDS = 2 * 60 * 60;

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public String issue(String username) {
        String token = UUID.randomUUID().toString().replace("-", "");
        sessions.put(token, new Session(username, Instant.now().plusSeconds(TTL_SECONDS)));
        return token;
    }

    public String resolveUsername(String token) {
        Session session = sessions.get(token);
        if (session == null) {
            return null;
        }
        if (session.expiresAt.isBefore(Instant.now())) {
            sessions.remove(token);
            return null;
        }
        return session.username;
    }

    public void revoke(String token) {
        sessions.remove(token);
    }

    private record Session(String username, Instant expiresAt) {
    }
}
