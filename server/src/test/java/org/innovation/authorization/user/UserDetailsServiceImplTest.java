package org.innovation.authorization.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.JUnitSoftAssertions;
import org.innovation.authorization.role.Role;
import org.innovation.authorization.user.model.User;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private User buildUserInfo(String username, String password, LocalDateTime accExp, LocalDateTime credExp) {
        User user = new User(username, password);
        user.setAccountExpiryDate(accExp);
        user.setCredentialsExpiryDate(credExp);
        return user;
    }

    @Test
    public void testLoadUserByUsername() {
        String username = "username";
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(buildUserInfo(username, "password", null, null)));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);

        softly.assertThat(userDetails.getUsername()).as(username).isEqualTo(username);
        softly.assertThat(userDetails.getPassword()).as("password").isEqualTo("password");
        softly.assertThat(userDetails.isEnabled()).as("account enabled").isTrue();
        softly.assertThat(userDetails.isAccountNonExpired()).as("account non expired").isTrue();
        softly.assertThat(userDetails.isCredentialsNonExpired()).as("credentials non expired").isTrue();
        softly.assertThat(userDetails.isAccountNonLocked()).as("account non locked").isTrue();
    }

    @Test
    public void testAccountExpiryForNullExpiryDate() {
        String username = "username";
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(buildUserInfo(username, "password", null, null)));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        assertThat(userDetails.isAccountNonExpired()).as("account non expired for no expiry date set").isTrue();
    }

    @Test
    public void testAccountExpiryForFutureExpiryDate() {
        String username = "username";
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(buildUserInfo(username, "password", LocalDateTime.now().plusDays(1), null)));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        assertThat(userDetails.isAccountNonExpired()).as("account non expired for expiry date in future").isTrue();
    }

    @Test
    public void testAccountExpiryForPastExpiryDate() {
        String username = "username";
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(buildUserInfo(username, "password", LocalDateTime.now().minusDays(1), null)));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        assertThat(userDetails.isAccountNonExpired()).as("account non expired for expiry date in past").isFalse();
    }

    @Test
    public void testCredentialsExpiryForNullExpiryDate() {
        String username = "username";
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(buildUserInfo(username, "password", null, null)));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        assertThat(userDetails.isCredentialsNonExpired()).as("credentials non expired for no expiry date set").isTrue();
    }

    @Test
    public void testCredentialsExpiryForFutureExpiryDate() {
        String username = "username";
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(buildUserInfo(username, "password", null, LocalDateTime.now().plusDays(1))));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        assertThat(userDetails.isCredentialsNonExpired()).as("credentials non expired for expiry date in future")
                .isTrue();
    }

    @Test
    public void testCredentialsExpiryForPastExpiryDate() {
        String username = "username";
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(buildUserInfo(username, "password", null, LocalDateTime.now().minusDays(1))));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        assertThat(userDetails.isCredentialsNonExpired()).as("credentials non expired for expiry date in past")
                .isFalse();
    }

    @Test
    public void testAccountLockedUnderMaxAttempts() {
        String username = "username";
        User userInfo = buildUserInfo(username, "password", null, null);
        userInfo.setFailedAttempts(userInfo.getMaxFailedAttempts() - 1);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userInfo));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        assertThat(userDetails.isAccountNonLocked()).as("account non locked for failed attempts under max attempts")
                .isTrue();
    }

    @Test
    public void testAccountLockedEqualMaxAttempts() {
        String username = "username";
        User userInfo = buildUserInfo(username, "password", null, null);
        userInfo.setFailedAttempts(userInfo.getMaxFailedAttempts());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userInfo));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        assertThat(userDetails.isAccountNonLocked()).as("account non locked for failed attempts equal to max attempts")
                .isFalse();
    }

    @Test
    public void testAccountLockedOverMaxAttempts() {
        String username = "username";
        User userInfo = buildUserInfo(username, "password", null, null);
        userInfo.setFailedAttempts(userInfo.getMaxFailedAttempts() + 1);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userInfo));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        assertThat(userDetails.isAccountNonLocked()).as("account non locked for failed attempts over max attempts")
                .isFalse();
    }

    @Test
    public void testAccountEnabled() {
        String username = "username";
        User userInfo = buildUserInfo(username, "password", null, null);
        userInfo.setEnabled(true);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userInfo));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        assertThat(userDetails.isEnabled()).as("account enabled flag when user info is enabled").isTrue();
    }

    @Test
    public void testAccountDisabled() {
        String username = "username";
        User userInfo = buildUserInfo(username, "password", null, null);
        userInfo.setEnabled(false);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userInfo));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        assertThat(userDetails.isEnabled()).as("account enabled flag when user info is disabled").isFalse();
    }

    @Test
    public void testGrantedAuthorities() {
        String username = "username";
        User userInfo = buildUserInfo(username, "password", null, null);
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("USER"));
        roles.add(new Role("MANAGER"));
        userInfo.setRoles(roles);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userInfo));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        assertThat(userDetails.getAuthorities()).extracting("role", "class").containsExactlyInAnyOrder(
                tuple("USER", SimpleGrantedAuthority.class), tuple("MANAGER", SimpleGrantedAuthority.class));
    }

    @Test
    public void testErrorOnNoUserFound() {
        String username = "username";
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> {
            userDetailsServiceImpl.loadUserByUsername(username);
        });

        verify(userRepository, times(1)).findByUsername(username);
        assertThat(thrown).as("thrown exception when user is not found").isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("No User with username: username could be found");
    }

}
