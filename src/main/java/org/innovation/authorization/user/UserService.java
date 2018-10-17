package org.innovation.authorization.user;

import java.time.LocalDateTime;
import java.util.HashSet;

import javax.annotation.Resource;

import org.innovation.authorization.role.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * service for interacting with the User entity
 * 
 * @author nick.bithrey
 *
 */
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
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("User with id [%d] not found", id)));
    }

    @Transactional(readOnly = false)
    public User save(User user) {
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
