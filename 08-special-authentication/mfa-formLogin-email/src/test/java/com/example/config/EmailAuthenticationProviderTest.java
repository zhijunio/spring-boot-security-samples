package com.example.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class EmailAuthenticationProviderTest {

    @Test
    void authenticateReturnsEmailFactor() {
        EmailCodeService codes = mock(EmailCodeService.class);
        EmailAuthenticationProvider provider = new EmailAuthenticationProvider(codes);
        EmailAuthenticationToken request = new EmailAuthenticationToken("user", "123456");

        Authentication result = provider.authenticate(request);

        verify(codes).consume("user", "123456");
        assertTrue(result.isAuthenticated());
        assertEquals("user", result.getName());
        assertEquals("FACTOR_EMAIL", result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void supportsOnlyEmailAuthenticationToken() {
        EmailAuthenticationProvider provider = new EmailAuthenticationProvider(new EmailCodeService());

        assertTrue(provider.supports(EmailAuthenticationToken.class));
    }
}
