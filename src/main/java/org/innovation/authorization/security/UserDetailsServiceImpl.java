package org.innovation.authorization.security;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Resource
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Optional<UserInfo> userInfoOpt = userRepository.findByUsername(username);
        UserInfo userInfo = userInfoOpt.orElseThrow(() -> new UsernameNotFoundException(
                String.format("No User with username: %s could be found", username)));
        LOGGER.debug("Found user {}", userInfo.getUsername());

        Set<SimpleGrantedAuthority> grantedAuthorities = userInfo.getRoles().stream()
                .peek(role -> LOGGER.trace("Adding role {} as authority", role.getName()))
                .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());

        return new User(userInfo.getUsername(), userInfo.getPassword(), userInfo.isEnabled(),
                !isAccountExpired(userInfo), !isCredentialsExpired(userInfo), !isAccountLocked(userInfo),
                grantedAuthorities);
    }

    private boolean isAccountExpired(UserInfo userInfo) {
        return userInfo.getAccountExpiryDate() != null
                && LocalDateTime.now().compareTo(userInfo.getAccountExpiryDate()) >= 0;
    }

    private boolean isCredentialsExpired(UserInfo userInfo) {
        return userInfo.getCredentialsExpiryDate() != null
                && LocalDateTime.now().compareTo(userInfo.getCredentialsExpiryDate()) >= 0;
    }

    private boolean isAccountLocked(UserInfo userInfo) {
        return userInfo.getMaxFailedAttempts() <= userInfo.getFailedAttempts();
    }
}
