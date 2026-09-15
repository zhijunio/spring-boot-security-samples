package com.example.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.springframework.security.authentication.BadCredentialsException;

public class BackupCodeService {

    public static final String USER_MNEMONIC =
            "payment noodle vivid slogan gather metal pilot enact fragile hip physical canvas";
    public static final String ADMIN_MNEMONIC =
            "movie shrimp volcano merge enforce sing alarm burst total plastic uncover duck";

    private final Bip39 bip39;
    private final Map<String, byte[]> hashes;

    public BackupCodeService(Bip39 bip39) {
        this.bip39 = bip39;
        bip39.validate(USER_MNEMONIC);
        bip39.validate(ADMIN_MNEMONIC);
        this.hashes = Map.of(
                "user", sha256(bip39.normalize(USER_MNEMONIC)),
                "admin", sha256(bip39.normalize(ADMIN_MNEMONIC)));
    }

    public void verify(String username, String mnemonic) {
        String normalized = bip39.normalize(mnemonic);
        try {
            bip39.validate(normalized);
        } catch (IllegalArgumentException ex) {
            throw new BadCredentialsException("Invalid BIP-39 mnemonic", ex);
        }
        byte[] expected = hashes.get(username);
        if (expected == null || !MessageDigest.isEqual(expected, sha256(normalized))) {
            throw new BadCredentialsException("Invalid backup phrase");
        }
    }

    private static byte[] sha256(String normalized) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(normalized.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
