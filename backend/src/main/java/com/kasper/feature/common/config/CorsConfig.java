package com.kasper.feature.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow all origins for development - in production, you'd want to restrict this
        config.addAllowedOrigin("http://localhost:5173"); // Frontend dev server
        config.addAllowedOrigin("http://localhost:5174"); // Alternative port
        config.addAllowedOrigin("http://localhost:8080"); // In case frontend is served from same origin
        
        // Allow all HTTP methods
        config.addAllowedMethod("*");
        
        // Allow all headers
        config.addAllowedHeader("*");
        
        // Allow credentials (cookies, authorization headers, etc.)
        config.setAllowCredentials(true);
        
        // How long the browser should cache the CORS response in seconds
        config.setMaxAge(3600L);
        
        // Apply this configuration to all paths
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}