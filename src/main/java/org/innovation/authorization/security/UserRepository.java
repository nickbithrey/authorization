package org.innovation.authorization.security;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserInfo, Long> {

    Optional<UserInfo> findByUsername(String username);

    @Modifying
    @Query("UPDATE UserInfo SET lastLoginDate = :date, failedAttempts = 0 WHERE username = :username")
    Integer updateUserOnSuccessfulLoginByUsername(@Param("username") String username,
            @Param("date") LocalDateTime date);

    @Modifying
    @Query("UPDATE UserInfo SET lastFailedLoginDate = :date, failedAttempts = failedAttempts + 1 WHERE username = :username")
    Integer updateUserOnFailedLoginByUsername(@Param("username") String username, @Param("date") LocalDateTime date);

}
