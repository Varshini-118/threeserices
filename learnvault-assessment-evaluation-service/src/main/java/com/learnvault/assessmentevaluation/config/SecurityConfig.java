package com.learnvault.assessmentevaluation.config;

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
                // INSTRUCTOR & ADMIN can create assessments and questions
                .requestMatchers("POST", "/api/assessments").hasAnyRole("INSTRUCTOR", "ADMIN")
                .requestMatchers("POST", "/api/assessments/*/questions").hasAnyRole("INSTRUCTOR", "ADMIN")
                .requestMatchers("DELETE", "/api/assessments/*/questions/*").hasAnyRole("INSTRUCTOR", "ADMIN")
                .requestMatchers("PATCH", "/api/assessments/*/status").hasAnyRole("INSTRUCTOR", "ADMIN")
                // LEARNER can submit attempts and view history
                .requestMatchers("POST", "/api/attempts").hasAnyRole("LEARNER", "ADMIN")
                .requestMatchers("GET", "/api/attempts").hasAnyRole("LEARNER", "INSTRUCTOR", "ADMIN")
                .requestMatchers("GET", "/api/attempts/**").hasAnyRole("LEARNER", "INSTRUCTOR", "ADMIN")
                // Anyone authenticated can view assessments
                .requestMatchers("GET", "/api/assessments").authenticated()
                .requestMatchers("GET", "/api/assessments/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}