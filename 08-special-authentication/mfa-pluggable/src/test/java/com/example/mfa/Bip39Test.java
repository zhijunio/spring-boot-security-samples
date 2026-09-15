package com.example.mfa;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

class Bip39Test {

    private final Bip39 bip39 = new Bip39();

    @Test
    void englishWordlistHas2048Words() {
        assertEquals(2048, bip39.words().size());
        assertEquals("abandon", bip39.words().getFirst());
        assertEquals("zoo", bip39.words().getLast());
    }

    @Test
    void generatedMnemonicIsValidAndReusable() {
        String mnemonic = bip39.generateMnemonic();
        assertTrue(bip39.isValid(mnemonic));
        assertDoesNotThrow(() -> bip39.validate(mnemonic));
        assertDoesNotThrow(() -> bip39.validate(mnemonic));
    }

    @Test
    void invalidChecksumIsRejected() {
        assertFalse(bip39.isValid(
                "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon"));
    }

}
