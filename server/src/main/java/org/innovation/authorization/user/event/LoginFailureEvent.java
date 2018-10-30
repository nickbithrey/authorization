package org.innovation.authorization.user.event;

/**
 * event for a failed login attempt
 * 
 * @author nick.bithrey
 *
 */
public class LoginFailureEvent extends LoginEvent {

    public LoginFailureEvent(String username) {
        super(username);
    }

}
