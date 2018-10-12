package org.innovation.authorization.security;

public class LoginFailureEvent extends LoginEvent {

    public LoginFailureEvent(String username) {
        super(username);
    }

}
