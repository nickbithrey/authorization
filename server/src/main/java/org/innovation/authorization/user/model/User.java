package org.innovation.authorization.user.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.innovation.authorization.BaseEntity;
import org.innovation.authorization.role.Role;

/**
 * Database Entity for the User
 *
 * @author nick.bithrey
 *
 */
@Entity
@Table(name = "USERDETAILS", uniqueConstraints = { @UniqueConstraint(columnNames = "USERNAME") })
public class User extends BaseEntity {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private boolean enabled = true;

    private LocalDateTime accountExpiryDate;

    private LocalDateTime credentialsExpiryDate;

    private LocalDateTime lastLoginDate;

    private LocalDateTime lastFailedLoginDate;

    @NotNull
    private Integer failedAttempts = 0;

    @NotNull
    private Integer maxFailedAttempts;

    @NotNull
    @ManyToMany
    @JoinTable(name = "userdetails_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    User() {
        // jpa use
    }

    public User(String username, String password) {
        super();
        this.username = username;
        this.password = password;
        this.maxFailedAttempts = 3;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getAccountExpiryDate() {
        return accountExpiryDate;
    }

    public void setAccountExpiryDate(LocalDateTime accountExpiryDate) {
        this.accountExpiryDate = accountExpiryDate;
    }

    public LocalDateTime getCredentialsExpiryDate() {
        return credentialsExpiryDate;
    }

    public void setCredentialsExpiryDate(LocalDateTime credentialsExpiryDate) {
        this.credentialsExpiryDate = credentialsExpiryDate;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public LocalDateTime getLastFailedLoginDate() {
        return lastFailedLoginDate;
    }

    public void setLastFailedLoginDate(LocalDateTime lastFailedLoginDate) {
        this.lastFailedLoginDate = lastFailedLoginDate;
    }

    public Integer getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(Integer failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public Integer getMaxFailedAttempts() {
        return maxFailedAttempts;
    }

    public void setMaxFailedAttempts(Integer maxFailedAttempts) {
        this.maxFailedAttempts = maxFailedAttempts;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
