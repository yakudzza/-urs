package org.example.hacaton.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hacaton.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(HEADER_NAME);
        log.info("Processing request to: {}", request.getRequestURI());
        log.info("Auth header exists: {}", authHeader != null);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("No valid auth header found");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(BEARER_PREFIX.length());
            String username = jwtService.extractUserName(jwt);
            String role = jwtService.extractRole(jwt);
            log.info("Token info - username: {}, role: {}", username, role);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(username);
                log.info("User details loaded - authorities: {}", userDetails.getAuthorities());

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Authentication set in context - authorities: {}", authToken.getAuthorities());
                } else {
                    log.error("Token validation failed");
                }
            }
        } catch (Exception e) {
            log.error("Error processing authentication: ", e);
        }

        filterChain.doFilter(request, response);
    }
}