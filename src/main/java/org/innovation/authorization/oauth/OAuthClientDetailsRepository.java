package org.innovation.authorization.oauth;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * repository for interacting with {@link OAuthClientDetails} entities
 *
 * @author nick.bithrey
 *
 */
public interface OAuthClientDetailsRepository extends JpaRepository<OAuthClientDetails, String> {

    OAuthClientDetails findClientDetailsByClientId(String clientId);

}
