package org.innovation.authorization.security;

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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
public class EventPublishingDaoAuthenticationProviderTest {

    @InjectMocks
    private EventPublishingDaoAuthenticationProvider attemptLoggingDaoAuthenticationProvider;

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
        attemptLoggingDaoAuthenticationProvider.authenticate(authentication);
        LocalDateTime to = LocalDateTime.now();

        verifyZeroInteractions(userService);
        verify(publisher).publishEvent(publishMatcher(new ArgumentMatcher<LoginSuccessEvent>() {

            @Override
            public boolean matches(LoginSuccessEvent event) {
                return "username".equals(event.getUsername())
                        && !(event.getTime().isBefore(from) || event.getTime().isAfter(to));
            }
        }, new LoginSuccessEvent("user")));
    }

    private <T> T publishMatcher(ArgumentMatcher<T> m, T result) {
        mockingProgress().getArgumentMatcherStorage().reportMatcher(m);
        return result;
    }

    @Test
    public void testAuthenticateFailureOnPasswordError() {
        when(passwordEncoder.matches(eq("password"), anyString())).thenReturn(false);

        LocalDateTime from = LocalDateTime.now();
        Throwable thrown = catchThrowable(() -> attemptLoggingDaoAuthenticationProvider.authenticate(authentication));
        LocalDateTime to = LocalDateTime.now();

        softly.assertThat(thrown).as("Exception thrown when password is incorrect in login")
                .isInstanceOf(BadCredentialsException.class).hasMessage("Bad credentials");
        verify(publisher).publishEvent(publishMatcher(new ArgumentMatcher<LoginFailureEvent>() {

            @Override
            public boolean matches(LoginFailureEvent event) {
                return "username".equals(event.getUsername())
                        && !(event.getTime().isBefore(from) || event.getTime().isAfter(to));
            }
        }, new LoginFailureEvent("user")));
    }

    @Test
    public void testAuthenticationFailureOnUserNotFoundError() {
        attemptLoggingDaoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        when(userDetailsService.loadUserByUsername(anyString())).thenThrow(new UsernameNotFoundException("message"));

        LocalDateTime from = LocalDateTime.now();
        Throwable thrown = catchThrowable(() -> attemptLoggingDaoAuthenticationProvider.authenticate(authentication));
        LocalDateTime to = LocalDateTime.now();

        softly.assertThat(thrown).as("Exception thrown when password is incorrect in login")
                .isInstanceOf(UsernameNotFoundException.class).hasMessage("message");
        verify(publisher).publishEvent(publishMatcher(new ArgumentMatcher<LoginFailureEvent>() {

            @Override
            public boolean matches(LoginFailureEvent event) {
                return "username".equals(event.getUsername())
                        && !(event.getTime().isBefore(from) || event.getTime().isAfter(to));
            }
        }, new LoginFailureEvent("user")));
    }

}
