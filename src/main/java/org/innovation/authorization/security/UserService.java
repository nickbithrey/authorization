package org.innovation.authorization.security;

import java.time.LocalDateTime;
import java.util.HashSet;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserRepository userRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public UserInfo getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("User with id [%d] not found", id)));
    }

    @Transactional(readOnly = false)
    public UserInfo save(UserInfo user) {
        LOGGER.trace("Encoding password");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        LOGGER.trace("Adding all roles to user");
        user.setRoles(new HashSet<>(roleRepository.findAll()));
        return userRepository.save(user);
    }

    @Transactional(readOnly = false)
    public Integer saveSuccessfulLoginAttempt(String username, LocalDateTime date) {
        LOGGER.trace("Updating user {} after successful login", username);
        return userRepository.updateUserOnSuccessfulLoginByUsername(username, date);
    }

    @Transactional(readOnly = false)
    public Integer saveFailedLoginAttempt(String username, LocalDateTime date) {
        LOGGER.trace("Updating user {} after failed login", username);
        return userRepository.updateUserOnFailedLoginByUsername(username, date);
    }
}
