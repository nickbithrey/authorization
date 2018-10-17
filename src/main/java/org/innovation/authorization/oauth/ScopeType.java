package org.innovation.authorization.oauth;

/**
 * enumeration for different scopes available for the application
 * 
 * @author nick.bithrey
 *
 */
public enum ScopeType implements OAuthEnumType {
    READ("read"),
    WRITE("write");

    private final String scopeName;

    private ScopeType(String scopeName) {
        this.scopeName = scopeName;
    }

    @Override
    public String extract() {
        return scopeName;
    }

}
