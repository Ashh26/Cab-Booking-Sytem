package com.yasif.project.uber.Uber.backend.system.configs;

import com.yasif.project.uber.Uber.backend.system.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
// Marks this class as a Spring configuration class.
// Spring will scan it and register the beans defined here.

@EnableWebSecurity
// Enables Spring Security for the application.
// This activates the security filter chain mechanism.

@RequiredArgsConstructor
// Lombok annotation that generates a constructor
// for all final fields (used for dependency injection).

@EnableMethodSecurity(securedEnabled = true)
// Enables method-level security using annotations like @Secured.
// Allows role-based access control directly on service/controller methods.

public class WebSecurityConfig {

    // Custom JWT authentication filter.
    // This filter will intercept requests and validate JWT tokens.
    private final JwtAuthFilter jwtAuthFilter;

    // Publicly accessible routes that do not require authentication.
    // Typically used for login, registration, or token generation APIs.
    private static final String[] PUBLIC_ROUTES = {"/auth/**"};

    @Bean
        // Exposes the SecurityFilterChain as a Spring Bean.
        // This defines how HTTP security is configured.
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity

                // Configures session management.
                // STATELESS means no HTTP session is created or used.
                // Every request must carry its own authentication (JWT).
                .sessionManagement(sessionConfig ->
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Disables CSRF protection.
                // Safe to disable for stateless REST APIs using JWT.
                .csrf(csrfConfig -> csrfConfig.disable())

                // Configures authorization rules for HTTP requests.
                .authorizeHttpRequests(auth ->

                        // Allows unauthenticated access to public routes.
                        auth.requestMatchers(PUBLIC_ROUTES).permitAll()

                                // Requires authentication for all other endpoints.
                                .anyRequest().authenticated()
                )

                // Adds the custom JWT filter before Spring’s default
                // UsernamePasswordAuthenticationFilter.
                // Ensures JWT validation happens early in the filter chain.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Builds and returns the configured SecurityFilterChain.
        return httpSecurity.build();
    }
}

