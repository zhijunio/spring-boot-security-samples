package com.example.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

public class BackupCodeAuthenticationProvider implements AuthenticationProvider {

    public static final String BACKUP_CODE_AUTHORITY = "FACTOR_BACKUP_CODE";

    private final BackupCodeService backupCodeService;

    public BackupCodeAuthenticationProvider(BackupCodeService backupCodeService) {
        this.backupCodeService = backupCodeService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        backupCodeService.verify(authentication.getName(), (String) authentication.getCredentials());
        return new BackupCodeAuthenticationToken(authentication.getName(), null, true);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BackupCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
