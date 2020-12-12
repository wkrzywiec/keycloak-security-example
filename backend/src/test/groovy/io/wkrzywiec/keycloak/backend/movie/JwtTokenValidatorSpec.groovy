package io.wkrzywiec.keycloak.backend.movie

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.wkrzywiec.keycloak.backend.infra.security.AccessToken
import io.wkrzywiec.keycloak.backend.infra.security.JwtTokenValidator
import io.wkrzywiec.keycloak.backend.infra.security.KeycloakJwkProvider
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Subject

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.RSAKeyGenParameterSpec
import java.time.Instant

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Subject(JwtTokenValidator)
class JwtTokenValidatorSpec extends Specification {

    private JwtTokenValidator validator
    private JwkProvider jwkProvider

    def setup() {
        jwkProvider = Stub(KeycloakJwkProvider)
        validator = new JwtTokenValidator(jwkProvider)
    }

    def "Get all movies (with Authorization header)"() {

        given: "Generate RSA Key Pair"
        KeyPair keypair = generateRsaKeyPair()

        and: "Generate correct JWT Access token"
        def token = generateAccessToken(keypair, "ADMIN")

        when: "Validate access token"
        def accessToken = validator.validateAuthorizationHeader(AccessToken.BEARER + token)

        then: "AccessToken has been created"
        accessToken.valueAsString == token
    }

    @Ignore("Different approach for token validation and generation")
    def "Try to get a single movie without Authorization header"() {

        when: "Make a call without Authorization header"
        def response = mockMvc.perform(
                get("/movies/1"))
                .andDo(print())

        then: "app returns 401 (unauthorized) code"
        response.andExpect(status().isUnauthorized())
    }

    @Ignore("Different approach for token validation and generation")
    def "Get a single movie (with Authorization header)"() {

        given: "Generate JWT Access token"
        def token = generateTokenWithRole("VISITOR")

        and: "Add JWT to request header"
        def request = get("/movies/1")
                .header("Authorization", "Bearer " + token)

        when: "Make a call without Authorization header"
        def response = mockMvc.perform(
                request)
                .andDo(print())

        then: "app returns 401 (unauthorized) code"
        response.andExpect(status().isOk())
    }


    private String generateTokenWithRole(String roleName) {
        Algorithm algorithm = Algorithm.HMAC256(secret)
        return JWT.create()
                .withIssuer("http://keycloak")
                .withExpiresAt(Date.from(Instant.now().plusSeconds(5 * 60)))
                .withClaim("scope", List.of("openid"))
                .withClaim("realm_access", Map.of("roles", List.of(roleName)))
                .sign(algorithm)
    }

    private KeyPair generateRsaKeyPair() {

        def keygen = KeyPairGenerator.getInstance("RSA")
        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4)
        keygen.initialize(spec)
        KeyPair keyPair = keygen.generateKeyPair()

        stubJsonWebKey(keyPair)
        return keyPair
    }

    private void stubJsonWebKey(KeyPair keyPair) {
        def jwk = Stub(Jwk)
        jwk.getPublicKey() >> keyPair.getPublic()
        jwkProvider.get(_) >> jwk
    }

    private String generateAccessToken(KeyPair keyPair, String roleName) {

        Algorithm algorithm = Algorithm.RSA256(keyPair.getPublic(), keyPair.getPrivate())
        return JWT.create()
                .withIssuer("http://keycloak")
                .withExpiresAt(Date.from(Instant.now().plusSeconds(5 * 60)))
                .withClaim("scope", List.of("openid"))
                .withClaim("realm_access", Map.of("roles", List.of(roleName)))
                .sign(algorithm)
    }
}