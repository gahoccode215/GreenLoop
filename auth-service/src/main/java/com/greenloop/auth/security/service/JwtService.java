package com.greenloop.auth.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    private static final String CLAIM_AUTHORITIES = "authorities";
    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_EMAIL = "email";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_AUTHORITIES, user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put(CLAIM_TYPE, TOKEN_TYPE_ACCESS);
        claims.put(CLAIM_EMAIL, user.getUsername());

        return createToken(claims, user.getUsername(), accessExpiration);
    }

    public String generateRefreshToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_TYPE, TOKEN_TYPE_REFRESH);
        claims.put(CLAIM_EMAIL, user.getUsername());

        return createToken(claims, user.getUsername(), refreshExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_TYPE, String.class));
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
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT token has expired", e);
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("JWT token is unsupported", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("JWT token is malformed", e);
        } catch (SignatureException e) {
            throw new RuntimeException("JWT signature validation failed", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT token is invalid", e);
        }
    }

    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public Boolean validateAccessToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            final String tokenType = extractTokenType(token);

            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token)
                    && TOKEN_TYPE_ACCESS.equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }




    public Long getAccessTokenExpirationSeconds() {
        return accessExpiration / 1000;
    }

    public Long getRefreshTokenExpirationSeconds() {
        return refreshExpiration / 1000;
    }

}

