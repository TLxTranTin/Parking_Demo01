package com.example.parking.shared.security;

import com.example.parking.auth.application.port.out.IJwtTokenProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final IJwtTokenProviderPort jwtTokenProviderPort;

    public JwtAuthenticationFilter(IJwtTokenProviderPort jwtTokenProviderPort) {
        this.jwtTokenProviderPort = jwtTokenProviderPort;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        if (!jwtTokenProviderPort.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtTokenProviderPort.extractUserId(token);
        String username = jwtTokenProviderPort.extractUsername(token);
        String rawRole = jwtTokenProviderPort.extractRole(token);

        String role = normalizeRole(rawRole);
        String authorityName = "ROLE_" + role;

        CurrentUserPrincipal principal = new CurrentUserPrincipal(
                userId,
                username,
                role
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        List.of(new SimpleGrantedAuthority(authorityName))
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "";
        }

        if (role.startsWith("ROLE_")) {
            return role.substring(5);
        }

        return role;
    }
}