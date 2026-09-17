package com.example.config;

import java.time.Instant;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.FactorGrantedAuthority;

public class TotpAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;
    private final String code;

    public TotpAuthenticationToken(String username, String code) {
        this(username, code, false);
    }

    TotpAuthenticationToken(String username, String code, boolean authenticated) {
        super(authenticated
                ? List.of(FactorGrantedAuthority.withAuthority(TotpAuthenticationProvider.TOTP_AUTHORITY)
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
