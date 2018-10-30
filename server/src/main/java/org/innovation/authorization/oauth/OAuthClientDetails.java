package org.innovation.authorization.oauth;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.innovation.authorization.BaseEntity;
import org.innovation.authorization.role.Role;

/**
 * entity for storing details of oauth clients
 * 
 * @author nick.bithrey
 *
 */
@Entity
@Table(name = "OCD", uniqueConstraints = { @UniqueConstraint(columnNames = "clientId") })
public class OAuthClientDetails extends BaseEntity {

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "ocd_resource", joinColumns = @JoinColumn(name = "ocd_id"))
    @Column(name = "resourceId")
    private Set<String> resourceIds = new HashSet<>();

    @ElementCollection(targetClass = ScopeType.class)
    @CollectionTable(name = "ocd_scope", joinColumns = @JoinColumn(name = "ocd_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "scope")
    private Set<ScopeType> scopes = new HashSet<>();

    @ElementCollection(targetClass = GrantType.class)
    @CollectionTable(name = "ocd_agt", joinColumns = @JoinColumn(name = "ocd_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "grant_type")
    private Set<GrantType> authorizedGrantTypes = new HashSet<>();

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "ocd_redirect", joinColumns = @JoinColumn(name = "ocd_id"))
    @Column(name = "redirect_uri")
    private Set<String> webServerRedirectUri = new HashSet<>();

    @NotNull
    @ManyToMany
    @JoinTable(name = "ocd_role", joinColumns = @JoinColumn(name = "ocd_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> authorities = new HashSet<>();

    private Integer accessTokenValidity;

    private Integer refreshTokenValidity;

    @Lob
    private String additionalInformation;

    @ElementCollection(targetClass = ScopeType.class)
    @CollectionTable(name = "ocd_aas", joinColumns = @JoinColumn(name = "ocd_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "scope")
    private Set<ScopeType> autoApproveScopes = new HashSet<>();

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Set<String> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(Set<String> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public Set<ScopeType> getScopes() {
        return scopes;
    }

    public void setScopes(Set<ScopeType> scopes) {
        this.scopes = scopes;
    }

    public Set<GrantType> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(Set<GrantType> authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public Set<String> getWebServerRedirectUri() {
        return webServerRedirectUri;
    }

    public void setWebServerRedirectUri(Set<String> webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
    }

    public Set<Role> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }

    public Integer getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setAccessTokenValidity(Integer accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public Integer getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(Integer refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public Set<ScopeType> getAutoApproveScopes() {
        return autoApproveScopes;
    }

    public void setAutoApproveScopes(Set<ScopeType> autoApproveScopes) {
        this.autoApproveScopes = autoApproveScopes;
    }

}
