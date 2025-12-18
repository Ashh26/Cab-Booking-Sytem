package com.yasif.project.uber.Uber.backend.system.security;


import com.yasif.project.uber.Uber.backend.system.entities.User;
import com.yasif.project.uber.Uber.backend.system.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
@Service
// Marks this class as a Spring-managed service component.
// Allows the filter to be injected into the security configuration.

@RequiredArgsConstructor
// Automatically creates a constructor for final fields
// enabling clean constructor-based dependency injection.

public class JwtAuthFilter extends OncePerRequestFilter {
    // Ensures this filter runs once per HTTP request.

    private final JWTService jwtService;
    // Handles JWT operations like token parsing and validation.

    private final UserService userService;
    // Used to fetch user details from the database.

    @Autowired
    @Qualifier("handlerExceptionResolver")
    // Injects Spring’s global exception resolver.
    // Allows consistent error responses even from filters.
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {

            // Reads the Authorization header from the incoming request.
            final String requestTokenHeader = request.getHeader("Authorization");

            // If the header is missing or does not start with "Bearer",
            // skip JWT processing and continue the filter chain.
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extracts the JWT token from the header.
            // Format expected: "Bearer <token>"
            String token = requestTokenHeader.split("Bearer ")[1];

            // Extracts userId from the JWT token.
            Long userId = jwtService.getUserIdFromToken(token);

            // Proceeds only if:
            // - userId is valid
            // - no authentication is already set in the security context
            if (userId != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // Loads user details from the database.
                User user = userService.getUserById(userId);

                // Creates an authentication token with user authorities.
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                // Attaches request-specific details (IP, session info, etc.).
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // Stores authentication in SecurityContext.
                // Marks the request as authenticated for downstream processing.
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authenticationToken);
            }

            // Continues the remaining filters in the chain.
            filterChain.doFilter(request, response);

        } catch (RuntimeException e) {

            // Delegates exception handling to Spring's global exception resolver.
            // Ensures uniform error response structure across controllers and filters.
            handlerExceptionResolver
                    .resolveException(request, response, null, e);
        }
    }
}

