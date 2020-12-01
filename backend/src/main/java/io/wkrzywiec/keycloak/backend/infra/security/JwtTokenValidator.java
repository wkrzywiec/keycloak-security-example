package io.wkrzywiec.keycloak.backend.infra.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Base64;

import static java.util.Objects.isNull;

@Component
@Slf4j
public class JwtTokenValidator {

    @Value("${keycloak.jwk}")
    private String jwkProviderUrl;

    public AccessToken validateAuthorizationHeader(String authorizationHeader) {
        String tokenValue = subStringBearer(authorizationHeader);
        validateToken(tokenValue);
        return new AccessToken(tokenValue);
    }

    private void validateToken(String value) {
        DecodedJWT decodedJWT = decodeToken(value);
        verifyTokenHeader(decodedJWT);
        verifySignature(decodedJWT);
        verifyPayload(decodedJWT);
    }

    private DecodedJWT decodeToken(String value) {
        if (isNull(value)){
            throw new InvalidTokenException("Token has not been provided");
        }
        return JWT.decode(value);
    }

    private void verifyTokenHeader(DecodedJWT decodedJWT) {
        try {
            Preconditions.checkArgument(decodedJWT.getType().equals("JWT"));
        } catch (IllegalArgumentException ex) {
            throw new InvalidTokenException("Token is not JWT type", ex);
        }
    }

    private void verifySignature(DecodedJWT decodedJWT) {
        try {
            JwkProvider provider = new KeycloakJwkProvider(jwkProviderUrl);
            Jwk jwk = provider.get(decodedJWT.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            algorithm.verify(decodedJWT);
        } catch (JwkException ex) {
            throw new InvalidTokenException("Token has invalid signature", ex);
        }
    }

    private void verifyPayload(DecodedJWT decodedJWT) {
            JsonObject payloadAsJson = decodeTokenPayloadToJsonObject(decodedJWT);
            if (hasTokenExpired(payloadAsJson)) {
                throw new InvalidTokenException("Token has expired");
            }

            if (!hasTokenRealmRolesClaim(payloadAsJson)) {
                throw new InvalidTokenException("Token doesn't contain claims with realm roles");
            }

            if (!hasTokenScopeInfo(payloadAsJson)) {
                throw new InvalidTokenException("Token doesn't contain scope information");
            }
    }

    private JsonObject decodeTokenPayloadToJsonObject(DecodedJWT decodedJWT) {
        try {
            String payloadAsString = decodedJWT.getPayload();
            return new Gson().fromJson(
                    new String(Base64.getDecoder().decode(payloadAsString), StandardCharsets.UTF_8),
                    JsonObject.class);
        }   catch (RuntimeException exception){
            throw new InvalidTokenException("Invalid JWT or JSON format of each of the jwt parts", exception);
        }
    }

    private boolean hasTokenExpired(JsonObject payloadAsJson) {
        Instant expirationDatetime = extractExpirationDate(payloadAsJson);
        return Instant.now().isAfter(expirationDatetime);
    }

    private Instant extractExpirationDate(JsonObject payloadAsJson) {
        try {
            return Instant.ofEpochSecond(payloadAsJson.get("exp").getAsLong());
        } catch (NullPointerException ex) {
            throw new InvalidTokenException("There is no 'exp' claim in the token payload");
        }
    }

    private boolean hasTokenRealmRolesClaim(JsonObject payloadAsJson) {
        try {
            return payloadAsJson.getAsJsonObject("realm_access").getAsJsonArray("roles").size() > 0;
        } catch (NullPointerException ex) {
            return false;
        }
    }

    private boolean hasTokenScopeInfo(JsonObject payloadAsJson) {
        return payloadAsJson.has("scope");
    }

    private String subStringBearer(String authorizationHeader) {
        return authorizationHeader.substring(AccessToken.BEARER.length());
    }
}