package io.wkrzywiec.keycloak.backend.infra.security.config;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.NetworkException;
import com.auth0.jwk.SigningKeyNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class KeycloakJwkProvider implements JwkProvider {

    private final URI uri;
    private final ObjectReader reader;

    public KeycloakJwkProvider(String jwkProviderUrl) {
        try {
            this.uri = new URI(jwkProviderUrl).normalize();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid jwks uri", e);
        }
        this.reader = new ObjectMapper().readerFor(Map.class);
    }

    @Override
    public Jwk get(String keyId) throws JwkException {
        final List<Jwk> jwks = getAll();
        if (keyId == null && jwks.size() == 1) {
            return jwks.get(0);
        }
        if (keyId != null) {
            for (Jwk jwk : jwks) {
                if (keyId.equals(jwk.getId())) {
                    return jwk;
                }
            }
        }
        throw new SigningKeyNotFoundException("No key found in " + uri.toString() + " with kid " + keyId, null);
    }

    private List<Jwk> getAll() throws SigningKeyNotFoundException {
        List<Jwk> jwks = Lists.newArrayList();
        final List<Map<String, Object>> keys = (List<Map<String, Object>>) getJwks().get("keys");

        if (keys == null || keys.isEmpty()) {
            throw new SigningKeyNotFoundException("No keys found in " + uri.toString(), null);
        }

        try {
            for (Map<String, Object> values : keys) {
                jwks.add(Jwk.fromValues(values));
            }
        } catch (IllegalArgumentException e) {
            throw new SigningKeyNotFoundException("Failed to parse jwk from json", e);
        }
        return jwks;
    }

    private Map<String, Object> getJwks() throws SigningKeyNotFoundException {
        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(this.uri)
                    .headers("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return reader.readValue(response.body());

        } catch (IOException | InterruptedException e) {
            throw new NetworkException("Cannot obtain jwks from url " + uri.toString(), e);
        }
    }
}
