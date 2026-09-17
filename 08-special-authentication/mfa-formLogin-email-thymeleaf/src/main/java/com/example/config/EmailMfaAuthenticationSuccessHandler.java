package com.example.config;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class EmailMfaAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final EmailCodeService emailCodeService;

    public EmailMfaAuthenticationSuccessHandler(EmailCodeService emailCodeService) {
        this.emailCodeService = emailCodeService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        IO.println("Email code for " + authentication.getName() + ": "
                + emailCodeService.issue(authentication.getName()));
        response.sendRedirect(request.getContextPath() + "/email/verify");
    }
}
