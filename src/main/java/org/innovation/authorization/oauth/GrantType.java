package org.innovation.authorization.oauth;

/**
 * enumeration for different grant types available. This is based off the grant types available in
 * oauth2
 *
 * @author nick.bithrey
 *
 */
public enum GrantType implements OAuthEnumType {

    AUTHORIZATION_CODE("authorization_code"),
    CLIENT_CREDENTIALS("client_credentials"),
    REFRESH_TOKEN("refresh_token"),
    PASSWORD("password"),
    IMPLICIT("implicit");

    private final String grantTypeStr;

    private GrantType(String grantTypeStr) {
        this.grantTypeStr = grantTypeStr;
    }

    @Override
    public String extract() {
        return grantTypeStr;
    }

}
