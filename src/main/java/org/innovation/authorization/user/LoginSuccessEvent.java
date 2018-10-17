package org.innovation.authorization.user;

/**
 * event for a successful login attempt
 * 
 * @author nick.bithrey
 *
 */
public class LoginSuccessEvent extends LoginEvent {

    public LoginSuccessEvent(String username) {
        super(username);
    }

}
