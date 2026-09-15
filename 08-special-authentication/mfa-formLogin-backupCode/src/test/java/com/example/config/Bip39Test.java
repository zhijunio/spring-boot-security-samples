package com.example.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Bip39Test {

    private static final String OFFICIAL_ZERO_ENTROPY =
            "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";

    private final Bip39 bip39 = new Bip39();

    @Test
    void englishWordlistHas2048OfficialWords() {
        assertEquals(2048, bip39.words().size());
        assertEquals("abandon", bip39.words().getFirst());
        assertEquals("zoo", bip39.words().getLast());
    }

    @Test
    void officialTwelveWordVectorIsValid() {
        assertTrue(bip39.isValid(OFFICIAL_ZERO_ENTROPY));
    }

    @Test
    void checksumFailureIsRejected() {
        assertFalse(bip39.isValid(
                "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon"));
        assertThrows(IllegalArgumentException.class, () -> bip39.validate(
                "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon"));
    }

    @Test
    void unknownWordIsRejected() {
        assertFalse(bip39.isValid(
                "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon notaword"));
    }

    @Test
    void whitespaceAndCaseAreNormalized() {
        assertEquals(OFFICIAL_ZERO_ENTROPY, bip39.normalize(
                "  ABANDON   Abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon ABOUT  "));
        assertTrue(bip39.isValid(
                "  ABANDON   Abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon ABOUT  "));
    }

    @Test
    void demoPhrasesAreValidBip39() {
        assertTrue(bip39.isValid(BackupCodeService.USER_MNEMONIC));
        assertTrue(bip39.isValid(BackupCodeService.ADMIN_MNEMONIC));
    }

}
