package com.example.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class BackupCodeAuthenticationProviderTest {

    @Test
    void authenticateReturnsBackupCodeFactor() {
        BackupCodeService backupCodeService = mock(BackupCodeService.class);
        BackupCodeAuthenticationProvider provider = new BackupCodeAuthenticationProvider(backupCodeService);
        BackupCodeAuthenticationToken request =
                new BackupCodeAuthenticationToken("user", BackupCodeService.USER_MNEMONIC);

        Authentication result = provider.authenticate(request);

        verify(backupCodeService).verify("user", BackupCodeService.USER_MNEMONIC);
        assertTrue(result.isAuthenticated());
        assertEquals("user", result.getName());
        assertEquals("FACTOR_BACKUP_CODE", result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void supportsOnlyBackupCodeAuthenticationToken() {
        BackupCodeAuthenticationProvider provider =
                new BackupCodeAuthenticationProvider(mock(BackupCodeService.class));

        assertTrue(provider.supports(BackupCodeAuthenticationToken.class));
    }

}
