package com.example.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

class TotpServiceTest {

    private final TotpService service = new TotpService(
            Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneOffset.UTC));

    @Test
    void currentCodeCanBeVerified() {
        String code = service.currentCode("user");

        assertDoesNotThrow(() -> service.verify("user", code));
    }

    @Test
    void wrongCodeIsRejected() {
        assertThrows(BadCredentialsException.class, () -> service.verify("user", "000000"));
    }

    @Test
    void unknownUserIsRejected() {
        assertThrows(BadCredentialsException.class, () -> service.verify("nobody", "123456"));
    }

    @Test
    void otpAuthUriContainsDemoSecret() {
        assertEquals(
                "otpauth://totp/mfa-formLogin-totp:user?secret=JBSWY3DPEHPK3PXP&issuer=mfa-formLogin-totp&algorithm=SHA1&digits=6&period=30",
                service.otpAuthUri("user"));
    }

}
