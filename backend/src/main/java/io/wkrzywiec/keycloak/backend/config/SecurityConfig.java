package io.wkrzywiec.keycloak.backend.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                    .authorizeRequests()
                        .antMatchers(HttpMethod.GET, "/actuator/**")
                        .permitAll()
                .anyRequest().authenticated()
                .and()
                    .oauth2ResourceServer()
                        .jwt();
    }
}
