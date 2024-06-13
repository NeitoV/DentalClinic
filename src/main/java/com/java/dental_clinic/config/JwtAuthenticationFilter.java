package com.java.dental_clinic.config;



import com.java.dental_clinic.service.JwtService;
import com.java.dental_clinic.service.impl.UserDetailsImpl.UserDetailsServiceImpl;
import io.jsonwebtoken.JwtException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);

            if (jwt != null) {
                String userName = jwtService.extractUsername(jwt);
                if (userName != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

                    if (jwtService.isTokenValid(jwt, userDetails)) {

                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }

            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            throw new IllegalStateException("Token cannot be trusted");
        }
    }

    private String parseJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            return authHeader.substring(7);
        }

        return null;
    }
}
