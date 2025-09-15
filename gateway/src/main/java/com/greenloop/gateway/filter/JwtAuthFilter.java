package com.greenloop.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Component
@Slf4j
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            log.info("path: {}", path);

            String authHeader = request.getHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("No Authorization header: {}", path);
                return onError(exchange);
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = validateToken(token);
                String username = claims.getSubject();

                log.info("Forwarding to downstream with X-User-Id: {}", username);
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", username)
                        .build();

                log.info("Continuing to next filter in chain");
                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                log.warn("JWT invalid: {}", e.getMessage());
                return onError(exchange);
            }
        };
    }

    private Claims validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.info("Token validated - User: {}, Exp: {}",
                    claims.getSubject(), claims.getExpiration());
            return claims;

        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw e;
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    public static class Config {}
}
