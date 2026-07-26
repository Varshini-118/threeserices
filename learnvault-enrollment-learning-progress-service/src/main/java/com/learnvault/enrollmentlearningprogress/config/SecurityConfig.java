package com.learnvault.enrollmentlearningprogress.config;

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
                // LEARNER can enroll, view own, update progress, complete
                .requestMatchers("POST", "/api/enrollments").hasAnyRole("LEARNER", "ADMIN")
                .requestMatchers("GET", "/api/enrollments").hasAnyRole("LEARNER", "INSTRUCTOR", "ADMIN")
                .requestMatchers("GET", "/api/enrollments/**").hasAnyRole("LEARNER", "INSTRUCTOR", "ADMIN")
                .requestMatchers("PATCH", "/api/enrollments/**").hasAnyRole("LEARNER", "ADMIN")
                // Module progress
                .requestMatchers("GET", "/api/enrollments/*/progress").hasAnyRole("LEARNER", "INSTRUCTOR", "ADMIN")
                .requestMatchers("PATCH", "/api/enrollments/*/progress/**").hasAnyRole("LEARNER", "ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}