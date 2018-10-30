package org.innovation.authorization.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

import java.time.LocalDateTime;
import java.util.Collections;

import org.assertj.core.api.JUnitSoftAssertions;
import org.innovation.authorization.user.event.LoginEvent;
import org.innovation.authorization.user.event.LoginFailureEvent;
import org.innovation.authorization.user.event.LoginSuccessEvent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
public class EventPublishingDaoAuthenticationProviderTest {

    @InjectMocks
    private EventPublishingDaoAuthenticationProvider eventPublishingDaoAuthenticationProvider;

    @Mock
    private UserService userService;

    @Mock
    private UserCache userCache;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private UsernamePasswordAuthenticationToken authentication;

    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setupMocks() {
        when(authentication.getName()).thenReturn("username");
        when(authentication.getCredentials()).thenReturn("password");
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(new User("username", "password", Collections.emptySet()));
    }

    @Test
    public void testAuthenticateSuccessful() {
        when(passwordEncoder.matches(eq("password"), anyString())).thenReturn(true);

        LocalDateTime from = LocalDateTime.now();
        eventPublishingDaoAuthenticationProvider.authenticate(authentication);
        LocalDateTime to = LocalDateTime.now();

        verifyZeroInteractions(userService);
        verify(publisher).publishEvent(
                publishMatcher(new LoginEventMatcher("username", from, to), new LoginSuccessEvent("user")));
    }

    @Test
    public void testAuthenticateFailureOnPasswordError() {
        when(passwordEncoder.matches(eq("password"), anyString())).thenReturn(false);

        LocalDateTime from = LocalDateTime.now();
        Throwable thrown = catchThrowable(() -> eventPublishingDaoAuthenticationProvider.authenticate(authentication));
        LocalDateTime to = LocalDateTime.now();

        softly.assertThat(thrown).as("Exception thrown when password is incorrect in login")
                .isInstanceOf(BadCredentialsException.class).hasMessage("Bad credentials");
        verify(publisher).publishEvent(
                publishMatcher(new LoginEventMatcher("username", from, to), new LoginFailureEvent("user")));
    }

    @Test
    public void testAuthenticationFailureOnUserNotFoundError() {
        eventPublishingDaoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        when(userDetailsService.loadUserByUsername(anyString())).thenThrow(new UsernameNotFoundException("message"));

        LocalDateTime from = LocalDateTime.now();
        Throwable thrown = catchThrowable(() -> eventPublishingDaoAuthenticationProvider.authenticate(authentication));
        LocalDateTime to = LocalDateTime.now();

        softly.assertThat(thrown).as("Exception thrown when password is incorrect in login")
                .isInstanceOf(UsernameNotFoundException.class).hasMessage("message");
        verify(publisher).publishEvent(
                publishMatcher(new LoginEventMatcher("username", from, to), new LoginFailureEvent("user")));
    }

    @Test
    public void testAfterPropsSet() throws Exception {
        EventPublishingDaoAuthenticationProvider provider = new EventPublishingDaoAuthenticationProvider(
                userDetailsService, passwordEncoder, publisher);
        Throwable thrown = catchThrowable(() -> provider.doAfterPropertiesSet());

        assertThat(thrown).as("exception thrown when checking properties when all are set").isNull();
    }

    @Test
    public void testAfterPropsSetNoUserDetailsService() throws Exception {
        EventPublishingDaoAuthenticationProvider provider = new EventPublishingDaoAuthenticationProvider(null,
                passwordEncoder, publisher);
        Throwable thrown = catchThrowable(() -> provider.doAfterPropertiesSet());

        assertThat(thrown).as("exception thrown when checking properties when userDetailsService is not set")
                .isInstanceOf(IllegalArgumentException.class).hasMessage("A UserDetailsService must be set");
    }

    @Test
    public void testAfterPropsSetNoPublisher() throws Exception {
        EventPublishingDaoAuthenticationProvider provider = new EventPublishingDaoAuthenticationProvider(
                userDetailsService, passwordEncoder, null);
        Throwable thrown = catchThrowable(() -> provider.doAfterPropertiesSet());

        assertThat(thrown).as("exception thrown when checking properties when publisher is not set")
                .isInstanceOf(IllegalArgumentException.class).hasMessage(
                        "%s must be defined to use this Authentication Provider. If this is not required, use %s instead",
                        ApplicationEventPublisher.class, DaoAuthenticationProvider.class);
    }

    @Test
    public void testInitializedWithNoPasswordEncoder() throws Exception {
        Throwable thrown = catchThrowable(
                () -> new EventPublishingDaoAuthenticationProvider(userDetailsService, null, publisher));

        assertThat(thrown).as("exception thrown when checking properties when passwordEncoder is not set")
                .isInstanceOf(IllegalArgumentException.class).hasMessage("passwordEncoder cannot be null");
    }

    private static class LoginEventMatcher implements ArgumentMatcher<LoginEvent> {

        private final String username;

        private final LocalDateTime from;

        private final LocalDateTime to;

        private LoginEventMatcher(String username, LocalDateTime from, LocalDateTime to) {
            super();
            this.username = username;
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean matches(LoginEvent event) {
            return username.equals(event.getUsername())
                    && !(event.getTime().isBefore(from) || event.getTime().isAfter(to));
        }
    }

    private <T> T publishMatcher(ArgumentMatcher<T> m, T result) {
        mockingProgress().getArgumentMatcherStorage().reportMatcher(m);
        return result;
    }

}
