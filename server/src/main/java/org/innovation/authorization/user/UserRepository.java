package org.innovation.authorization.user;

import java.time.LocalDateTime;
import java.util.Optional;

import org.innovation.authorization.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Modifying
    @Query("UPDATE User SET lastLoginDate = :date, failedAttempts = 0 WHERE username = :username")
    Integer updateUserOnSuccessfulLoginByUsername(@Param("username") String username,
            @Param("date") LocalDateTime date);

    @Modifying
    @Query("UPDATE User SET lastFailedLoginDate = :date, failedAttempts = failedAttempts + 1 WHERE username = :username")
    Integer updateUserOnFailedLoginByUsername(@Param("username") String username, @Param("date") LocalDateTime date);

}
