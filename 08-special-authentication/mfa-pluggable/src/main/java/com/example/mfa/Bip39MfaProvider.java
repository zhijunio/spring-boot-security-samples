package com.example.mfa;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
public class Bip39MfaProvider implements MfaProvider {

    public static final String ID = "bip39";

    private final Bip39 bip39;

    public Bip39MfaProvider(Bip39 bip39) {
        this.bip39 = bip39;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String displayName() {
        return "BIP-39 recovery phrase";
    }

    @Override
    public String credentialPlaceholder() {
        return "12-word phrase";
    }

    @Override
    public void verify(String username, String payload, String credential) {
        String normalized = bip39.normalize(credential);
        try {
            bip39.validate(normalized);
        } catch (IllegalArgumentException ex) {
            throw new BadCredentialsException("Invalid BIP-39 mnemonic", ex);
        }
        if (!MessageDigest.isEqual(HexFormat.of().parseHex(payload), sha256(normalized))) {
            throw new BadCredentialsException("Invalid recovery phrase");
        }
    }

    @Override
    public BindingMaterial beginBinding(String username) {
        return new BindingMaterial(ID, bip39.generateMnemonic());
    }

    @Override
    public String completeBinding(String username, BindingMaterial material, String confirmation) {
        String normalized = bip39.normalize(confirmation == null || confirmation.isBlank()
                ? material.secret()
                : confirmation);
        bip39.validate(normalized);
        return HexFormat.of().formatHex(sha256(normalized));
    }

    public String hashOf(String mnemonic) {
        String normalized = bip39.normalize(mnemonic);
        bip39.validate(normalized);
        return HexFormat.of().formatHex(sha256(normalized));
    }

    private static byte[] sha256(String normalized) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(normalized.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
