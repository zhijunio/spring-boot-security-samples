package com.example.config;

import java.io.IOException;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        IO.println("\n\nCustomAuthenticationFilter Header: " + "x-forbidden:" + request.getHeader("x-forbidden"));
        if (Objects.isNull(request.getHeader("x-forbidden"))) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }
        if (Objects.equals(request.getHeader("x-forbidden"), "no")) {
            response.setHeader("x-forbidden", "no");
            filterChain.doFilter(request, response);
        }
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setHeader("x-forbidden", "yes");
        return;

    }

}
