package com.learnvault.instructorsessionmanagement.config;

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
                // ADMIN can manage instructors
                .requestMatchers("POST", "/api/instructors").hasRole("ADMIN")
                .requestMatchers("PATCH", "/api/instructors/*/status").hasRole("ADMIN")
                .requestMatchers("PATCH", "/api/instructors/*/rating").hasRole("ADMIN")
                // INSTRUCTOR & ADMIN can schedule/cancel sessions
                .requestMatchers("POST", "/api/training-sessions").hasAnyRole("INSTRUCTOR", "ADMIN")
                .requestMatchers("PATCH", "/api/training-sessions/*/cancel").hasAnyRole("INSTRUCTOR", "ADMIN")
                // LEARNER can register for sessions
                .requestMatchers("POST", "/api/session-registrations").hasAnyRole("LEARNER", "ADMIN")
                // INSTRUCTOR & ADMIN can mark attendance
                .requestMatchers("PATCH", "/api/session-registrations/*/attendance").hasAnyRole("INSTRUCTOR", "ADMIN")
                // All authenticated can view
                .requestMatchers("GET", "/api/instructors").authenticated()
                .requestMatchers("GET", "/api/instructors/**").authenticated()
                .requestMatchers("GET", "/api/training-sessions").authenticated()
                .requestMatchers("GET", "/api/training-sessions/**").authenticated()
                .requestMatchers("GET", "/api/session-registrations").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}