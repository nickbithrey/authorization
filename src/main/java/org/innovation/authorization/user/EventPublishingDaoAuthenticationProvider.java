package org.innovation.authorization.user;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * component to publish an event based on the authentication result (success or failure).
 *
 * @author nick.bithrey
 *
 */
@Component
public class EventPublishingDaoAuthenticationProvider extends DaoAuthenticationProvider {

    private ApplicationEventPublisher publisher;

    public EventPublishingDaoAuthenticationProvider(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder, ApplicationEventPublisher publisher) {
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);
        this.publisher = publisher;
    }

    @Override
    protected void doAfterPropertiesSet() throws Exception {
        super.doAfterPropertiesSet();
        Assert.notNull(publisher, String.format(
                "%s must be defined to use this Authentication Provider. If this is not required, use %s instead",
                ApplicationEventPublisher.class, DaoAuthenticationProvider.class));
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        try {
            return super.authenticate(authentication);
        } catch (AuthenticationException e) {
            publisher.publishEvent(new LoginFailureEvent(authentication.getName()));
            throw e;
        }
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
            UserDetails user) {
        Authentication auth = super.createSuccessAuthentication(principal, authentication, user);
        publisher.publishEvent(new LoginSuccessEvent(auth.getName()));
        return auth;
    }

}
