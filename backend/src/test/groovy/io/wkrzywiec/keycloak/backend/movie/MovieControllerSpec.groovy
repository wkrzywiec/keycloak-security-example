package io.wkrzywiec.keycloak.backend.movie

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import spock.lang.Specification

import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import java.time.Instant

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
class MovieControllerSpec extends Specification {

    @LocalServerPort
    int randomServerPort

    @Autowired
    TestRestTemplate restTemplate

    String baseUrl
    static String secret = "slFGcSDerewcSDF34cscDSFsde45sSDF"

    def setup(){
        baseUrl = "http://localhost:" + randomServerPort
    }

    def "Try to get all movies without Authorization header"() {

        when: "Make a call without Authorization header"
        def response = restTemplate.getForEntity(baseUrl + "/movies", String.class)

        then: "app returns 401 (unauthorized) code"
        response.getStatusCode() == HttpStatus.UNAUTHORIZED
    }

    def "Get all movies (with Authorization header)"() {

        given: "Generate JWT Access token"
        def token = generateToken()

        and: "Add JWT to request header"
        HttpHeaders headers = new HttpHeaders()
        headers.set("Authorization", "Bearer " + token)
        HttpEntity request = new HttpEntity<>( headers)

        when: "Make a call without Authorization header"
        def response = restTemplate.exchange(baseUrl + "/movies", HttpMethod.GET, request, String.class)


        then: "app returns 401 (unauthorized) code"
        response.statusCode == HttpStatus.OK
    }

    def "Try to get a single movie without Authorization header"() {

        when: "Make a call without Authorization header"
        def response = restTemplate.getForEntity(baseUrl + "/movies/1", String.class)

        then: "app returns 401 (unauthorized) code"
        response.getStatusCode() == HttpStatus.UNAUTHORIZED
    }

    def "Get a single movie (with Authorization header)"() {

        given: "Generate JWT Access token"
        def token = generateToken()

        and: "Add JWT to request header"
        HttpHeaders headers = new HttpHeaders()
        headers.set("Authorization", "Bearer " + token)
        HttpEntity request = new HttpEntity<>( headers)

        when: "Make a call without Authorization header"
        def response = restTemplate.exchange(baseUrl + "/movies/1", HttpMethod.GET, request, String.class)

        then: "app returns 401 (unauthorized) code"
        response.getStatusCode() == HttpStatus.OK
    }

    private String generateToken() {
        Algorithm algorithm = Algorithm.HMAC256(secret)
        return JWT.create()
                .withIssuer("http://keycloak")
                .withExpiresAt(Date.from(Instant.now().plusSeconds(5 * 60)))
                .sign(algorithm)
    }

    @TestConfiguration
    static class TestSecurityConfig{
        @Bean
        JwtDecoder jwtDecoder() {
            byte[] encoded = secret.getBytes()
            SecretKey secretKey = new SecretKeySpec(encoded, "HmacSHA256")
            return NimbusJwtDecoder.withSecretKey(secretKey).build();
        }
    }
}