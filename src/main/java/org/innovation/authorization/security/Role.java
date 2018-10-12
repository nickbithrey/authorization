package org.innovation.authorization.security;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

import org.innovation.authorization.BaseEntity;

@Entity
@Table(name = "ROLE", uniqueConstraints = { @UniqueConstraint(columnNames = "NAME") })
public class Role extends BaseEntity {

    @NotBlank
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<UserInfo> users;

    Role() {
        // jpa use
    }

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(Set<UserInfo> users) {
        this.users = users;
    }

}
