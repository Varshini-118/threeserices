package com.learnvault.coursecatalogcontentmanagement.config;

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
                // Anyone authenticated can VIEW published courses
                .requestMatchers("/api/courses").authenticated()
                .requestMatchers("/api/courses/**").authenticated()
                .requestMatchers("/api/learning-paths").authenticated()
                .requestMatchers("/api/learning-paths/**").authenticated()
                // Only ADMIN can create, publish, archive, delete courses & paths
                .requestMatchers("POST", "/api/courses").hasRole("ADMIN")
                .requestMatchers("PATCH", "/api/courses/*/publish").hasRole("ADMIN")
                .requestMatchers("PATCH", "/api/courses/*/archive").hasRole("ADMIN")
                .requestMatchers("DELETE", "/api/courses/*").hasRole("ADMIN")
                .requestMatchers("POST", "/api/courses/*/modules").hasRole("ADMIN")
                .requestMatchers("PUT", "/api/courses/*/modules/*").hasRole("ADMIN")
                .requestMatchers("DELETE", "/api/courses/*/modules/*").hasRole("ADMIN")
                .requestMatchers("POST", "/api/learning-paths").hasRole("ADMIN")
                .requestMatchers("PATCH", "/api/learning-paths/*/status").hasRole("ADMIN")
                .requestMatchers("DELETE", "/api/learning-paths/*").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}