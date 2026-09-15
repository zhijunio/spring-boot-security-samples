package com.example.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

class BackupCodeServiceTest {

    private final BackupCodeService service = new BackupCodeService(new Bip39());

    @Test
    void matchingPhraseCanBeReused() {
        assertDoesNotThrow(() -> service.verify("user", BackupCodeService.USER_MNEMONIC));
        assertDoesNotThrow(() -> service.verify("user", BackupCodeService.USER_MNEMONIC));
    }

    @Test
    void validBip39PhraseForAnotherUserIsRejected() {
        assertThrows(BadCredentialsException.class,
                () -> service.verify("user", BackupCodeService.ADMIN_MNEMONIC));
    }

    @Test
    void invalidChecksumIsRejected() {
        assertThrows(BadCredentialsException.class, () -> service.verify("user",
                "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon"));
    }

    @Test
    void unknownUserIsRejected() {
        assertThrows(BadCredentialsException.class,
                () -> service.verify("nobody", BackupCodeService.USER_MNEMONIC));
    }

}
