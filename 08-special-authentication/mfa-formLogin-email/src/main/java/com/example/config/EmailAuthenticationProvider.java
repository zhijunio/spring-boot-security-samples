package com.example.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

public class EmailAuthenticationProvider implements AuthenticationProvider {

    public static final String EMAIL_AUTHORITY = "FACTOR_EMAIL";

    private final EmailCodeService emailCodeService;

    public EmailAuthenticationProvider(EmailCodeService emailCodeService) {
        this.emailCodeService = emailCodeService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        emailCodeService.consume(authentication.getName(), (String) authentication.getCredentials());
        return new EmailAuthenticationToken(authentication.getName(), null, true);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
