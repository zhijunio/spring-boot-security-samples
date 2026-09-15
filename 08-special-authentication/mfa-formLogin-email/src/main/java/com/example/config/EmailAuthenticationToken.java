package com.example.config;

import java.util.List;
import java.time.Instant;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.FactorGrantedAuthority;

public class EmailAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;
    private final String code;

    public EmailAuthenticationToken(String username, String code) {
        this(username, code, false);
    }

    EmailAuthenticationToken(String username, String code, boolean authenticated) {
        super(authenticated
                ? List.of(FactorGrantedAuthority.withAuthority(EmailAuthenticationProvider.EMAIL_AUTHORITY)
                .issuedAt(Instant.now()).build())
                : List.of());
        this.username = username;
        this.code = code;
        if (authenticated) {
            super.setAuthenticated(true);
        }
    }

    @Override
    public Object getCredentials() {
        return code;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}
