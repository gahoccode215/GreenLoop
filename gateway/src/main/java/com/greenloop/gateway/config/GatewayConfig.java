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

                // 1. Public Auth Routes (NO JWT) - Phải đặt trước
                .route("auth-public", r -> r
                        .path("/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/auth/test")
                        .uri("lb://auth-service"))

                // 2. Public Routes (NO JWT)
                .route("public-routes", r -> r
                        .path("/api/v1/public/**")
                        .uri("lb://auth-service"))

                // 3. Protected Routes (REQUIRE JWT) - Đặt sau
                .route("protected-routes", r -> r
                        .path("/api/v1/auth/info", "/api/v1/private/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())))
                        .uri("lb://auth-service"))

                // 4. User Service (REQUIRE JWT)
                .route("user-service", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())))
                        .uri("lb://user-service"))

                .build();
    }
}
