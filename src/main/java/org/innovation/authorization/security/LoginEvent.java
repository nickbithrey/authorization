package org.innovation.authorization.security;

import java.time.LocalDateTime;

public abstract class LoginEvent {

    private final String username;

    private final LocalDateTime time;

    public LoginEvent(String username) {
        this(username, LocalDateTime.now());
    }

    public LoginEvent(String username, LocalDateTime time) {
        super();
        this.username = username;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getTime() {
        return time;
    }

}
