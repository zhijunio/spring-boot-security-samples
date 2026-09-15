package com.example.config;

import java.io.IOException;

import com.example.mfa.MfaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class PluggableMfaAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MfaService mfaService;

    public PluggableMfaAuthenticationSuccessHandler(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (mfaService.requiresSecondFactor(authentication.getName())) {
            mfaService.prepareIfNeeded(authentication.getName());
            response.sendRedirect(request.getContextPath() + "/mfa/challenge");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/");
    }

}
