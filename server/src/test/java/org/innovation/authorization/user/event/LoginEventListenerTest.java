package org.innovation.authorization.user.event;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.innovation.authorization.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginEventListenerTest {

    @InjectMocks
    private LoginEventListener listener;

    @Mock
    private UserService userService;

    @Test
    public void testSuccessfulLoginAttempt() {
        LoginSuccessEvent event = mock(LoginSuccessEvent.class);
        String username = "username";
        when(event.getUsername()).thenReturn(username);
        LocalDateTime time = LocalDateTime.now();
        when(event.getTime()).thenReturn(time);
        listener.handleLoginSuccessEvent(event);

        verify(userService, times(1)).saveSuccessfulLoginAttempt(username, time);
        verify(event, atLeastOnce()).getUsername();
        verify(event, atLeastOnce()).getTime();
    }

    @Test
    public void testFailedLoginAttempt() {
        LoginFailureEvent event = mock(LoginFailureEvent.class);
        String username = "username";
        when(event.getUsername()).thenReturn(username);
        LocalDateTime time = LocalDateTime.now();
        when(event.getTime()).thenReturn(time);
        listener.handleLoginFailureEvent(event);

        verify(userService, times(1)).saveFailedLoginAttempt(username, time);
        verify(event, atLeastOnce()).getUsername();
        verify(event, atLeastOnce()).getTime();
    }
}
