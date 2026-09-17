package com.example.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

class EmailCodeServiceTest {

    private final EmailCodeService service = new EmailCodeService();

    @Test
    void issuedCodeCanBeConsumedOnce() {
        String code = service.issue("user");

        assertDoesNotThrow(() -> service.consume("user", code));
        assertThrows(BadCredentialsException.class, () -> service.consume("user", code));
    }

    @Test
    void wrongCodeIsRejected() {
        service.issue("user");

        assertThrows(BadCredentialsException.class, () -> service.consume("user", "000000"));
    }
}
