package io.wkrzywiec.keycloak.backend.movie


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import spock.lang.Ignore
import spock.lang.Specification

import java.time.Instant

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MovieControllerSpec extends Specification {

    @Ignore("Different approach for token validation and generation")
    def "Get all movies (with Authorization header)"() {

        given: "Generate JWT Access token"
        def token = generateTokenWithRole("ADMIN")

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

//    @TestConfiguration
//    static class TestSecurityConfig{
//        @Bean
//        JwtDecoder jwtDecoder() {
//            byte[] encoded = secret.getBytes()
//            SecretKey secretKey = new SecretKeySpec(encoded, "HmacSHA256")
//            return NimbusJwtDecoder.withSecretKey(secretKey).build();
//        }
//    }
}