package com.example.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

public class CompromisedPasswordAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler("/");
    private final CompromisedPasswordChecker passwordChecker;

    public CompromisedPasswordAwareAuthenticationSuccessHandler(CompromisedPasswordChecker passwordChecker) {
        this.passwordChecker = passwordChecker;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (passwordChecker.check((String) authentication.getCredentials()).isCompromised()) {
            request.getSession(true).setAttribute("compromised_password", true);
        }
        successHandler.onAuthenticationSuccess(request, response, authentication);
    }
}
