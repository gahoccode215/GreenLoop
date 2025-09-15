package com.greenloop.auth.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    private SecretKey getKey() { return Keys.hmacShaKeyFor(secret.getBytes()); }

    public String generateAccessToken(UserDetails user) {
        Map<String,Object> claims = Map.of("authorities", user.getAuthorities(), "type","access");
        return createToken(claims, user.getUsername(), accessExpiration);
    }

    public String generateRefreshToken(UserDetails user) {
        Map<String,Object> claims = Map.of("type","refresh");
        return createToken(claims, user.getUsername(), refreshExpiration);
    }

    private String createToken(Map<String,Object> claims, String sub, Long expiry){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(sub)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiry))
                .signWith(getKey())
                .compact();
    }

    /* === TOKEN EXTRACTION === */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /* === TOKEN VALIDATION === */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateAccessToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        final String tokenType = extractTokenType(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && "access".equals(tokenType));
    }

    public Boolean validateRefreshToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        final String tokenType = extractTokenType(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && "refresh".equals(tokenType));
    }

    /* === UTILITY === */
    public Long getAccessTokenExpirationSeconds(){
        return accessExpiration/1000;
    }

    public Long getRefreshTokenExpirationSeconds(){
        return refreshExpiration/1000;
    }
}

