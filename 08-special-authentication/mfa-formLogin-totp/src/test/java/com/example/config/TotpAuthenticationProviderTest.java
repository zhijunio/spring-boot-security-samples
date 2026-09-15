package com.example.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class TotpAuthenticationProviderTest {

    @Test
    void authenticateReturnsTotpFactor() {
        TotpService totpService = mock(TotpService.class);
        TotpAuthenticationProvider provider = new TotpAuthenticationProvider(totpService);
        TotpAuthenticationToken request = new TotpAuthenticationToken("user", "123456");

        Authentication result = provider.authenticate(request);

        verify(totpService).verify("user", "123456");
        assertTrue(result.isAuthenticated());
        assertEquals("user", result.getName());
        assertEquals("FACTOR_TOTP", result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void supportsOnlyTotpAuthenticationToken() {
        TotpAuthenticationProvider provider = new TotpAuthenticationProvider(new TotpService());

        assertTrue(provider.supports(TotpAuthenticationToken.class));
    }

}
