package io.wkrzywiec.keycloak.backend.movie

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import java.time.Instant

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    String baseUrl
    static String secret = "slFGcSDerewcSDF34cscDSFsde45sSDF"

    def "Try to get all movies without Authorization header"() {

        when: "Make a call without Authorization header"
        def response = mockMvc.perform(
                get("/movies"))
                .andDo(print())

        then: "app returns 401 (unauthorized) code"
        response.andExpect(status().isUnauthorized())
    }

    def "Get all movies (with Authorization header)"() {

        given: "Generate JWT Access token"
        def token = generateToken()

        and: "Add JWT to request header"
        def request = get("/movies")
                .header("Authorization", "Bearer " + token)

        when: "Make a call without Authorization header"
        def response = mockMvc.perform(
                request)
                .andDo(print())

        then: "app returns 200 (OK) code"
        response.andExpect(status().isOk())
    }

    def "Try to get a single movie without Authorization header"() {

        when: "Make a call without Authorization header"
        def response = mockMvc.perform(
                get("/movies/1"))
                .andDo(print())

        then: "app returns 401 (unauthorized) code"
        response.andExpect(status().isUnauthorized())
    }

    def "Get a single movie (with Authorization header)"() {

        given: "Generate JWT Access token"
        def token = generateToken()

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