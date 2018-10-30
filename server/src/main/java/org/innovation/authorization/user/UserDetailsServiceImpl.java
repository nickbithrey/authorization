package org.innovation.authorization.user;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Resource;

import org.innovation.authorization.role.RoleAuthorityUtil;
import org.innovation.authorization.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * service for reading the user details from database
 *
 * @see UserDetailsService
 * @author nick.bithrey
 *
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Resource
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Optional<User> userInfoOpt = userRepository.findByUsername(username);
        User user = userInfoOpt.orElseThrow(() -> new UsernameNotFoundException(
                String.format("No User with username: %s could be found", username)));
        LOGGER.debug("Found user {}", user.getUsername());

        Collection<GrantedAuthority> grantedAuthorities = RoleAuthorityUtil.extractRoleAuths(user.getRoles());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.isEnabled(), !isAccountExpired(user), !isCredentialsExpired(user), !isAccountLocked(user),
                grantedAuthorities);
    }

    private boolean isAccountExpired(User user) {
        return user.getAccountExpiryDate() != null && LocalDateTime.now().compareTo(user.getAccountExpiryDate()) >= 0;
    }

    private boolean isCredentialsExpired(User userInfo) {
        return userInfo.getCredentialsExpiryDate() != null
                && LocalDateTime.now().compareTo(userInfo.getCredentialsExpiryDate()) >= 0;
    }

    private boolean isAccountLocked(User userInfo) {
        return userInfo.getMaxFailedAttempts() <= userInfo.getFailedAttempts();
    }
}
