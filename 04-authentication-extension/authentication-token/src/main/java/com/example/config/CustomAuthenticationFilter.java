package com.example.config;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        IO.println("\n\nCustomAuthenticationFilter Header: " + "x-custom-secret:" + request.getHeader("x-custom-secret"));
        if (!Collections.list(request.getHeaderNames()).contains("x-custom-secret")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!Objects.equals(request.getHeader("x-custom-secret"), "spring-boot-tutorial")) {
            IO.println("FORBIDDEN");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }
        CustomAuthenticationToken auth = new CustomAuthenticationToken();
        SecurityContext newContext = SecurityContextHolder.createEmptyContext();
        newContext.setAuthentication(auth);
        SecurityContextHolder.setContext(newContext);

        filterChain.doFilter(request, response);

    }

}
