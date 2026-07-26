package com.learnvault.certificationbadgemanagement.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // ADMIN can create badges, revoke certs, award badges
                .requestMatchers("POST", "/api/badges").hasRole("ADMIN")
                .requestMatchers("PATCH", "/api/certifications/*/revoke").hasRole("ADMIN")
                .requestMatchers("POST", "/api/badge-awards").hasAnyRole("ADMIN")
                // Anyone authenticated can view
                .requestMatchers("GET", "/api/badges").authenticated()
                .requestMatchers("GET", "/api/badges/**").authenticated()
                .requestMatchers("GET", "/api/certifications").authenticated()
                .requestMatchers("GET", "/api/certifications/**").authenticated()
                .requestMatchers("GET", "/api/badge-awards").authenticated()
                // POST certifications is called internally by ELP via Feign (permitAll for internal)
                .requestMatchers("POST", "/api/certifications").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}