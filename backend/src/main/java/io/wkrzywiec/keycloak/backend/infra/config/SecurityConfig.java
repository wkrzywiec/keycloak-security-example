package io.wkrzywiec.keycloak.backend.infra.config;

import io.wkrzywiec.keycloak.backend.infra.security.AccessTokenAuthenticationFailureHandler;
import io.wkrzywiec.keycloak.backend.infra.security.JwtTokenFilter;
import io.wkrzywiec.keycloak.backend.infra.security.JwtTokenValidator;
import io.wkrzywiec.keycloak.backend.infra.security.KeycloakAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Order(1)
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.security.ignored}")
    private String nonSecureUrl;

    private final KeycloakAuthenticationProvider authenticationProvider;
    private final JwtTokenValidator tokenVerifier;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(new JwtTokenFilter(tokenVerifier, authenticationManagerBean(), authenticationFailureHandler()), BasicAuthenticationFilter.class)
                .csrf().disable()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(nonSecureUrl).permitAll()
                .anyRequest().authenticated();
    }

    @ConditionalOnMissingBean
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AccessTokenAuthenticationFailureHandler();
    }
}
