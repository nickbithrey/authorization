package org.innovation.authorization.security;

public class LoginSuccessEvent extends LoginEvent {

    public LoginSuccessEvent(String username) {
        super(username);
    }

}
