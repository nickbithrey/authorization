package org.innovation.authorization.oauth;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.innovation.authorization.role.RoleAuthorityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * service for reading the oauth {@link ClientDetails} from the database
 *
 * @see ClientDetailsService
 * @author nick.bithrey
 *
 */
@Component
public class OAuth2ClientDetailsService implements ClientDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2ClientDetailsService.class);

    private OAuthClientDetailsRepository oauthClientDetailsRepository;

    public OAuth2ClientDetailsService(OAuthClientDetailsRepository oauthClientDetailsRepository) {
        super();
        this.oauthClientDetailsRepository = oauthClientDetailsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDetails loadClientByClientId(String clientId) {
        OAuthClientDetails oauthClientDetails = oauthClientDetailsRepository.findClientDetailsByClientId(clientId);
        BaseClientDetails cd = new BaseClientDetails();
        cd.setClientId(oauthClientDetails.getClientId());
        cd.setClientSecret(oauthClientDetails.getClientSecret());
        cd.setResourceIds(oauthClientDetails.getResourceIds());
        cd.setScope(extractTypes(oauthClientDetails.getScopes()));
        cd.setAuthorizedGrantTypes(extractTypes(oauthClientDetails.getAuthorizedGrantTypes()));
        cd.setAuthorities(RoleAuthorityUtil.extractRoleAuths(oauthClientDetails.getAuthorities()));
        cd.setRegisteredRedirectUri(oauthClientDetails.getWebServerRedirectUri());
        cd.setAccessTokenValiditySeconds(oauthClientDetails.getAccessTokenValidity());
        cd.setRefreshTokenValiditySeconds(oauthClientDetails.getRefreshTokenValidity());
        cd.setAdditionalInformation(extractAdditionalInformation(oauthClientDetails.getAdditionalInformation()));
        cd.setAutoApproveScopes(extractTypes(oauthClientDetails.getAutoApproveScopes()));
        return cd;
    }

    private Map<String, ?> extractAdditionalInformation(String additionalInformation) {
        if (!StringUtils.hasText(additionalInformation)) {
            return Collections.emptyMap();
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(additionalInformation, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            LOGGER.error(String.format("Error in parsing additional information to map [%s]", additionalInformation),
                    e);
            throw new InternalAuthenticationServiceException("Could not parse additional information of oauth client",
                    e);
        }
    }

    public <T extends OAuthEnumType> Collection<String> extractTypes(Collection<T> types) {
        return types.stream().map(this::extractType).collect(Collectors.toSet());
    }

    public String extractType(OAuthEnumType type) {
        return type.extract();
    }
}
