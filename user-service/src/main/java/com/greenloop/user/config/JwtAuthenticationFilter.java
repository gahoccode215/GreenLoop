package com.greenloop.user.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Get user info from Gateway headers
        String userId = request.getHeader("X-User-Id");
        String authorities = request.getHeader("X-User-Authorities");

        if (userId != null && authorities != null) {
            // Create authentication object
            List<SimpleGrantedAuthority> authList = List.of(new SimpleGrantedAuthority("ROLE_USER"));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authList);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Authenticated user: {} with authorities: {}", userId, authorities);
        }

        filterChain.doFilter(request, response);
    }
}