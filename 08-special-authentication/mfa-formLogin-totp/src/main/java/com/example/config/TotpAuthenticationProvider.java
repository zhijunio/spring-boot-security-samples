package com.example.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

public class TotpAuthenticationProvider implements AuthenticationProvider {

    public static final String TOTP_AUTHORITY = "FACTOR_TOTP";

    private final TotpService totpService;

    public TotpAuthenticationProvider(TotpService totpService) {
        this.totpService = totpService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        totpService.verify(authentication.getName(), (String) authentication.getCredentials());
        return new TotpAuthenticationToken(authentication.getName(), null, true);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TotpAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
