package com.greenloop.gateway.config;

import com.greenloop.gateway.filter.JwtAuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder, JwtAuthFilter jwtAuthFilter) {
        return builder.routes()
                .route("auth-public", r -> r
                        .path("/api/v1/auth/login", "/api/v1/auth/register")
                        .uri("lb://auth-service"))

                .route("user-service", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())))
                        .uri("lb://user-service"))

                .build();
    }
}
