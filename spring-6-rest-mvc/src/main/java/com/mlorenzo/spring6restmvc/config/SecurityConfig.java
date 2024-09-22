package com.mlorenzo.spring6restmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Profile("!test")
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                // Nota: La expresión "/v3/api-docs**" es para permitir también el acceso a "/v3/api-docs.yaml"
                .requestMatchers(HttpMethod.GET,"/v3/api-docs**", "/swagger-ui.html", "/swagger-ui/*")
                    .permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // Se comenta esta línea porque ahora esta aplicación es un servidor de recursos OAuth2 que usa tokens JWT
                //.httpBasic();
                .oauth2ResourceServer().jwt();
        return http.build();
    }
}
