package io.wkrzywiec.keycloak.backend.movie

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.wkrzywiec.keycloak.backend.infra.security.AccessToken
import io.wkrzywiec.keycloak.backend.infra.security.InvalidTokenException
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

    def "Create AccessToken from String value"() {

        given: "Generate RSA Key Pair"
        KeyPair keyPair = generateRsaKeyPair()
        stubJsonWebKey(keyPair)

        and: "Generate correct JWT Access token"
        def token = generateAccessToken(keyPair, "ADMIN")

        when: "Validate access token"
        def accessToken = validator.validateAuthorizationHeader(AccessToken.BEARER + token)

        then: "AccessToken has been created"
        accessToken.valueAsString == token
    }

    def "AccessToken with incorrect signature"() {

        given: "Generate first RSA Key Pair"
        KeyPair firstKeyPair = generateRsaKeyPair()

        and: "Generate second RSA Key Pair"
        KeyPair secondKeyPair = generateRsaKeyPair()
        stubJsonWebKey(secondKeyPair)

        and: "Generate JWT Access token"
        def token = generateAccessToken(firstKeyPair, "ADMIN")

        when: "Validate access token"
        validator.validateAuthorizationHeader(AccessToken.BEARER + token)

        then: "Token has invalid signature"
        def exception = thrown(InvalidTokenException)
        exception.message == 'Token has invalid signature'
    }

    //no role info
    //no scope info
    //expired token
    //verify issuer

    private KeyPair generateRsaKeyPair() {

        def keygen = KeyPairGenerator.getInstance("RSA")
        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4)
        keygen.initialize(spec)
        KeyPair keyPair = keygen.generateKeyPair()

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