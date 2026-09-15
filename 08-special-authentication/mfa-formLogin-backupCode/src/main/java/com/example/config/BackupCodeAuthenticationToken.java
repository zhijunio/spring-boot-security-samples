package com.example.config;

import java.time.Instant;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.FactorGrantedAuthority;

public class BackupCodeAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;
    private final String mnemonic;

    public BackupCodeAuthenticationToken(String username, String mnemonic) {
        this(username, mnemonic, false);
    }

    BackupCodeAuthenticationToken(String username, String mnemonic, boolean authenticated) {
        super(authenticated
                ? List.of(FactorGrantedAuthority.withAuthority(BackupCodeAuthenticationProvider.BACKUP_CODE_AUTHORITY)
                .issuedAt(Instant.now()).build())
                : List.of());
        this.username = username;
        this.mnemonic = mnemonic;
        if (authenticated) {
            super.setAuthenticated(true);
        }
    }

    @Override
    public Object getCredentials() {
        return mnemonic;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

}
