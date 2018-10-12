package org.innovation.authorization.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testGetUser() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1l);
        when(userRepository.findById(userInfo.getId())).thenReturn(Optional.of(userInfo));

        UserInfo user = userService.getUser(userInfo.getId());

        assertThat(user).as("returned user").isEqualTo(userInfo);
        verify(userRepository).findById(userInfo.getId());
    }

    @Test
    public void testGetUserWhenNotFound() {
        long userInfoId = 1l;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable exception = catchThrowable(() -> userService.getUser(userInfoId));

        assertThat(exception).as("thrown exception when no user found").isInstanceOfAny(IllegalArgumentException.class)
                .hasMessage("User with id [1] not found");
        verify(userRepository).findById(userInfoId);
    }

    @Test
    public void testSave() {
        UserInfo userInfo = new UserInfo();
        String rawPassword = "pass";
        userInfo.setPassword(rawPassword);
        Role role = new Role();
        role.setName("USER");
        String encoded = "encoded";
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> encoded + invocation.getArgument(0));
        when(roleRepository.findAll()).thenReturn(Arrays.asList(role));
        when(userRepository.save(any(UserInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userInfo = userService.save(userInfo);

        softly.assertThat(userInfo.getPassword()).as("saved user info password").isEqualTo(encoded + rawPassword);
        softly.assertThat(userInfo.getRoles()).as("saved user info roles").hasSize(1).contains(role);
        verify(passwordEncoder).encode(rawPassword);
        verify(roleRepository).findAll();
        verify(userRepository).save(userInfo);
    }

    @Test
    public void testSaveSuccessfulLoginAttempt() {
        String username = "username";
        LocalDateTime date = LocalDateTime.now();
        int updatedInt = 1;
        when(userRepository.updateUserOnSuccessfulLoginByUsername(anyString(), any(LocalDateTime.class)))
                .thenReturn(updatedInt);

        Integer update = userService.saveSuccessfulLoginAttempt(username, date);

        assertThat(update).as("result from query").isEqualTo(updatedInt);
        verify(userRepository).updateUserOnSuccessfulLoginByUsername(username, date);
    }

    @Test
    public void testSaveFailedLoginAttempt() {
        String username = "username";
        LocalDateTime date = LocalDateTime.now();
        int updatedInt = 1;
        when(userRepository.updateUserOnFailedLoginByUsername(anyString(), any(LocalDateTime.class)))
                .thenReturn(updatedInt);

        Integer update = userService.saveFailedLoginAttempt(username, date);

        assertThat(update).as("result from query").isEqualTo(updatedInt);
        verify(userRepository).updateUserOnFailedLoginByUsername(username, date);
    }

}
