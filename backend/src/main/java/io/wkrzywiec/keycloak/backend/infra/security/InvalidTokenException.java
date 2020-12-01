package io.wkrzywiec.keycloak.backend.infra.security;

import org.springframework.security.core.AuthenticationException;

public class InvalidTokenException extends AuthenticationException {
    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
