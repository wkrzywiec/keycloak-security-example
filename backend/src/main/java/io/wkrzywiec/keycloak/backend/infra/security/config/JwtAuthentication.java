package io.wkrzywiec.keycloak.backend.infra.security.config;

import io.wkrzywiec.keycloak.backend.infra.security.AccessToken;
import lombok.ToString;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@ToString
public class JwtAuthentication extends AbstractAuthenticationToken {

    private final AccessToken accessToken;

    public JwtAuthentication(AccessToken accessToken) {
        super(accessToken.getAuthorities());
        this.accessToken = accessToken;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return accessToken.getValueAsString();
    }
}
