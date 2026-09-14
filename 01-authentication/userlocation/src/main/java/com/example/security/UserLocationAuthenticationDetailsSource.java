package com.example.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationDetailsSource;

public class UserLocationAuthenticationDetailsSource
        implements AuthenticationDetailsSource<HttpServletRequest, UserLocationAuthenticationDetails> {

    @Override
    public UserLocationAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new UserLocationAuthenticationDetails(context);
    }
}
