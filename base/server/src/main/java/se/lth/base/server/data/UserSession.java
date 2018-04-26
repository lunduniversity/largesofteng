package se.lth.base.server.data;

import java.util.UUID;

public class UserSession {

    private final UUID sessionId;
    private final User user;

    public UserSession(UUID sessionId, User user) {
        this.sessionId = sessionId;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public UUID getSessionId() {
        return sessionId;
    }
}
