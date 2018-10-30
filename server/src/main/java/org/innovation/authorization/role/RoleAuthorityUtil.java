package org.innovation.authorization.role;

import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * static methods for manipulating the {@link Role}s
 * 
 * @author nick.bithrey
 *
 */
public class RoleAuthorityUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleAuthorityUtil.class);

    private RoleAuthorityUtil() {
        // static methods only
    }

    public static final Collection<GrantedAuthority> extractRoleAuths(Collection<Role> roles) {
        return roles.stream().peek(role -> LOGGER.trace("Adding role {} as authority", role.getName()))
                .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());
    }

}
