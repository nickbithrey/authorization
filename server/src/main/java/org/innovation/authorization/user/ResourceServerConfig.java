package org.innovation.authorization.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.util.Assert;

/**
 * configuration for the resource server to be secured
 *
 * @author nick.bithrey
 *
 */
@Configuration
@EnableResourceServer
@Order(SecurityProperties.BASIC_AUTH_ORDER - 1)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String SCOPE_FORMAT = "#oauth2.hasScope(%s)";

    private static final String API_URL = "/api/**";

    private static String access(String... scopes) {
        Assert.notEmpty(scopes, "must have scopes to set access rules");
        return String.format(SCOPE_FORMAT, "'" + StringUtils.join(scopes, "','") + "'");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String readScope = "read";
        String writeScope = "write";
        // @formatter:off
        http.requestMatchers().antMatchers(API_URL)
        .and().authorizeRequests()
            .antMatchers(HttpMethod.GET, API_URL).access(access(readScope))
            .antMatchers(HttpMethod.POST, API_URL).access(access(writeScope))
            .antMatchers(HttpMethod.PATCH, API_URL).access(access(writeScope))
            .antMatchers(HttpMethod.PUT, API_URL).access(access(writeScope))
            .antMatchers(HttpMethod.DELETE, API_URL).access(access(writeScope))
            .antMatchers(HttpMethod.GET, "/webjars/**").permitAll()
            .antMatchers(HttpMethod.POST, "/perform_login").permitAll();
        // @formatter:on
    }

}
