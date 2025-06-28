package com.tlfdt.bonrecreme.security.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global Web MVC configuration for the application.
 * This class provides a centralized place for web-related beans and settings,
 * including the global Cross-Origin Resource Sharing (CORS) policy.
 */
@Configuration
public class WebConfig {

    /**
     * Defines a global CORS configuration for the entire application.
     * This is a more robust alternative to configuring CORS within Spring Security,
     * as it applies to all controllers handled by WebMVC.
     *
     * @return A WebMvcConfigurer with the defined CORS mappings.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Apply to all API endpoints
                        .allowedOrigins("http://localhost:5174") // Allow your frontend origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
                        .allowedHeaders("*") // Allow all headers
                        .allowCredentials(true); // Allow cookies and credentials
            }
        };
    }
}
