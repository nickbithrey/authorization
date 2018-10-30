package org.innovation.authorization.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.api.JUnitSoftAssertions;
import org.innovation.authorization.role.Role;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

@RunWith(MockitoJUnitRunner.class)
public class OAuth2ClientDetailsServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2ClientDetailsServiceTest.class);

    @InjectMocks
    private OAuth2ClientDetailsService oAuth2ClientDetailsService;

    @Mock
    private OAuthClientDetailsRepository oauthClientDetailsRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testLoadClientByClientId() throws Exception {
        String clientId = "clientId";
        when(oauthClientDetailsRepository.findClientDetailsByClientId(clientId)).thenReturn(buildOAuthClient());

        ClientDetails clientDetails = oAuth2ClientDetailsService.loadClientByClientId(clientId);

        softly.assertThat(clientDetails).as("found client details")
                .isEqualToComparingFieldByFieldRecursively(expectedClientDetails());
        verify(oauthClientDetailsRepository, times(1)).findClientDetailsByClientId(clientId);
    }

    private OAuthClientDetails buildOAuthClient() {
        OAuthClientDetails oauthClientDetails = new OAuthClientDetails();
        oauthClientDetails.setClientId("clientId");
        oauthClientDetails.setClientSecret("clientSecret");
        oauthClientDetails.setResourceIds(Collections.singleton("resourceId1"));
        oauthClientDetails.setScopes(Collections.singleton(ScopeType.READ));
        oauthClientDetails.setAuthorizedGrantTypes(Collections.singleton(GrantType.AUTHORIZATION_CODE));
        oauthClientDetails.setAuthorities(Collections.singleton(new Role("USER")));
        oauthClientDetails.setWebServerRedirectUri(Collections.singleton("redirect1"));
        oauthClientDetails.setAccessTokenValidity(100);
        oauthClientDetails.setRefreshTokenValidity(200);
        oauthClientDetails.setAdditionalInformation("");
        oauthClientDetails.setAutoApproveScopes(Collections.singleton(ScopeType.READ));
        return oauthClientDetails;
    }

    private BaseClientDetails expectedClientDetails() {
        BaseClientDetails oauthClientDetails = new BaseClientDetails();
        oauthClientDetails.setClientId("clientId");
        oauthClientDetails.setClientSecret("clientSecret");
        oauthClientDetails.setResourceIds(Collections.singleton("resourceId1"));
        oauthClientDetails.setScope(Collections.singleton("read"));
        oauthClientDetails.setAuthorizedGrantTypes(Collections.singleton("authorization_code"));
        oauthClientDetails.setAuthorities(Collections.singleton(new SimpleGrantedAuthority("USER")));
        oauthClientDetails.setRegisteredRedirectUri(Collections.singleton("redirect1"));
        oauthClientDetails.setAccessTokenValiditySeconds(100);
        oauthClientDetails.setRefreshTokenValiditySeconds(200);
        oauthClientDetails.setAdditionalInformation(new HashMap<>());
        oauthClientDetails.setAutoApproveScopes(Collections.singleton("read"));
        return oauthClientDetails;
    }

    @Test
    public void testExtractAdditionalInformation() {
        String clientId = "clientId";
        OAuthClientDetails oauthClientDetails = new OAuthClientDetails();
        String additionalInformationStr = "{\"key\":\"value\"}";
        oauthClientDetails.setAdditionalInformation(additionalInformationStr);
        when(oauthClientDetailsRepository.findClientDetailsByClientId(clientId)).thenReturn(oauthClientDetails);

        ClientDetails clientDetails = oAuth2ClientDetailsService.loadClientByClientId(clientId);

        assertThat(clientDetails.getAdditionalInformation())
                .as("additional information on client details parsed from %s", additionalInformationStr)
                .containsOnly(entry("key", "value"));
    }

    @Test
    public void testExtractAdditionalInformationNonParseable() {
        String clientId = "clientId";
        OAuthClientDetails oauthClientDetails = new OAuthClientDetails();
        // invalid additional information string (no closing "}")
        String additionalInformationStr = "{\"key\":\"value\"";
        oauthClientDetails.setAdditionalInformation(additionalInformationStr);
        when(oauthClientDetailsRepository.findClientDetailsByClientId(clientId)).thenReturn(oauthClientDetails);

        Throwable thrown = catchThrowable(() -> oAuth2ClientDetailsService.loadClientByClientId(clientId));

        assertThat(thrown)
                .as("Exception thrown when parsing invalid additional information string [%s]",
                        additionalInformationStr)
                .isInstanceOf(InternalAuthenticationServiceException.class)
                .hasMessage("Could not parse additional information of oauth client [%s]", additionalInformationStr);
    }

    @Test
    public void testExtractScopes() {
        String clientId = "clientId";
        OAuthClientDetails oauthClientDetails = new OAuthClientDetails();
        Set<ScopeType> scopes = EnumSet.allOf(ScopeType.class);
        oauthClientDetails.setScopes(scopes);
        when(oauthClientDetailsRepository.findClientDetailsByClientId(clientId)).thenReturn(oauthClientDetails);

        ClientDetails clientDetails = oAuth2ClientDetailsService.loadClientByClientId(clientId);

        assertThat(clientDetails.getScope()).as("scope on client details parsed from %s", scopes)
                .containsOnlyElementsOf(scopes.stream().map(ScopeType::extract).collect(Collectors.toSet()));
    }

    @Test
    public void testExtractAutoApproveScopes() {
        String clientId = "clientId";
        OAuthClientDetails oauthClientDetails = new OAuthClientDetails();
        Set<ScopeType> scopes = EnumSet.allOf(ScopeType.class);
        oauthClientDetails.setAutoApproveScopes(scopes);
        when(oauthClientDetailsRepository.findClientDetailsByClientId(clientId)).thenReturn(oauthClientDetails);

        ClientDetails clientDetails = oAuth2ClientDetailsService.loadClientByClientId(clientId);

        scopes.stream().forEach(scope -> {
            softly.assertThat(clientDetails.isAutoApprove(scope.extract()))
                    .as("scope:[%s] is set to auto approve", scope).isTrue();
        });
    }
}
