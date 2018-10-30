package org.innovation.authorization.user.event;

import org.innovation.authorization.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * listener for login events and processes these events
 *
 * @author nick.bithrey
 *
 */
@Component
@EnableAsync
public class LoginEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginEventListener.class);

    private UserService userService;

    public LoginEventListener(UserService userService) {
        this.userService = userService;
    }

    /**
     * handles a successful login
     *
     * @param event
     */
    @EventListener
    @Async
    public void handleLoginSuccessEvent(LoginSuccessEvent event) {
        LOGGER.info("{} user successful login attempt", event.getUsername());
        userService.saveSuccessfulLoginAttempt(event.getUsername(), event.getTime());
    }

    /**
     * handles the occurance when a login failure attempt has been made.
     *
     * <p>
     * This cannot be asyncronous because the update of the record must be within the current thread
     * otherwise the publishing thread could be slow enough for the subsequent logins be made before
     * the failure attempt has been logged
     * </p>
     *
     * @param event
     */
    @EventListener
    public void handleLoginFailureEvent(LoginFailureEvent event) {
        LOGGER.info("{} user failed login attempt", event.getUsername());
        userService.saveFailedLoginAttempt(event.getUsername(), event.getTime());
    }
}
