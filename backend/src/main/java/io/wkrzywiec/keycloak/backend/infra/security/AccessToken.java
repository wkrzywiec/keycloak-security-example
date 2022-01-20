package io.wkrzywiec.keycloak.backend.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;

public record AccessToken(String value) {

    public static final String BEARER = "Bearer ";

    public String getValue() {
        return value;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        JsonObject payloadAsJson = getPayloadAsJsonObject();

        return StreamSupport.stream(
                        payloadAsJson.getAsJsonObject("realm_access").getAsJsonArray("roles").spliterator(), false)
                .map(JsonElement::getAsString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String getUsername() {
        JsonObject payloadAsJson = getPayloadAsJsonObject();

        return Optional.ofNullable(
                        payloadAsJson.getAsJsonPrimitive("preferred_username").getAsString())
                .orElse("");
    }

    private JsonObject getPayloadAsJsonObject() {
        DecodedJWT decodedJWT = decodeToken(value);
        return decodeTokenPayloadToJsonObject(decodedJWT);
    }

    private DecodedJWT decodeToken(String value) {
        if (isNull(value)) {
            throw new InvalidTokenException("Token has not been provided");
        }
        return JWT.decode(value);
    }

    private JsonObject decodeTokenPayloadToJsonObject(DecodedJWT decodedJWT) {
        try {
            String payloadAsString = decodedJWT.getPayload();
            return new Gson().fromJson(
                    new String(Base64.getDecoder().decode(payloadAsString), StandardCharsets.UTF_8),
                    JsonObject.class);
        } catch (RuntimeException exception) {
            throw new InvalidTokenException("Invalid JWT or JSON format of each of the jwt parts", exception);
        }
    }
}
