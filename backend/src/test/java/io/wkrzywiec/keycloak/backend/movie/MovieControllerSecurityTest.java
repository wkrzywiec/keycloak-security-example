package io.wkrzywiec.keycloak.backend.movie;

import com.auth0.jwk.JwkProvider;
import io.wkrzywiec.keycloak.backend.infra.security.KeycloakJwkProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class MovieControllerSecurityTest {

    @Container
    static GenericContainer keycloak = new GenericContainer(DockerImageName.parse("jboss/keycloak:11.0.2"))
            .withExposedPorts(8080)
            .withEnv("KEYCLOAK_USER", "admin")
            .withEnv("KEYCLOAK_PASSWORD", "admin")
            .withEnv("DB_VENDOR", "h2")
            .withEnv("KEYCLOAK_IMPORT", "/tmp/realm-test.json")
            .withClasspathResourceMapping("keycloak/realm-test.json", "/tmp/realm-test.json", BindMode.READ_ONLY)
            .withCommand("-Dkeycloak.profile.feature.upload_scripts=enabled")
            .waitingFor(Wait.forHttp("/auth/realms/master"));


    @Configuration
    static class TestConfiguration {

        @Bean
        @Primary
        public JwkProvider keycloakJwkProvider() {
            String jwkUrl = "http://" + keycloak.getHost() + ":" + keycloak.getMappedPort(8080) + "/auth/realms/master/protocol/openid-connect/certs";
            return new KeycloakJwkProvider(jwkUrl);
        }
    }

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("Try to get all movies without Authorization header")
    void getAllMoviesWithoutAuthorizationHeader() throws Exception {

        mockMvc.perform(
                get("/movies"))
                .andDo(print()).andExpect(status().isUnauthorized());
    }
}
