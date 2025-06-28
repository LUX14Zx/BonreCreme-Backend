package com.tlfdt.bonrecreme.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.Map;

/**
 * Main security configuration for the application.
 * This class enables web security and configures basic security settings.
 * NOTE: JWT authentication has been removed. The current configuration permits all
 * requests and should be updated with an appropriate security model for production.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    /**
     * Defines the security filter chain that applies to all HTTP requests.
     *
     * @param http The {@link HttpSecurity} to configure.
     * @return The configured {@link SecurityFilterChain}.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless APIs
                .csrf(AbstractHttpConfigurer::disable)

                // Configure exception handling for access denied errors
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler())
                )

                // Configure authorization rules
                .authorizeHttpRequests(authorize -> authorize
                        // Permit access to Swagger UI and API docs
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // WARNING: This permits all other requests.
                        // Add specific authorization rules here for a secure application.
                        .anyRequest().permitAll()
                )

                // Ensure session management is stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );


        return http.build();
    }

    /**
     * Provides a custom {@link AccessDeniedHandler} to return a structured JSON error
     * for HTTP 403 (Forbidden) responses.
     *
     * @return The access denied handler.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            Map<String, Object> errorDetails = Map.of(
                    "status", "error",
                    "statusCode", HttpServletResponse.SC_FORBIDDEN,
                    "message", "Access Denied: You do not have the required role to perform this action."
            );
            response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
        };
    }
}
