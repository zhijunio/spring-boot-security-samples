package com.example.mfa;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
public class EmailMfaProvider implements MfaProvider {

    public static final String ID = "email";

    private final Map<String, Code> codes = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String displayName() {
        return "Email code";
    }

    @Override
    public boolean needsPrepare() {
        return true;
    }

    @Override
    public boolean needsBindingConfirmation() {
        return true;
    }

    @Override
    public String credentialPlaceholder() {
        return "6-digit code";
    }

    @Override
    public void prepare(String username, String payload) {
        String code = "%06d".formatted(random.nextInt(1_000_000));
        codes.put(username, new Code(code, Instant.now().plusSeconds(300)));
        IO.println("Email MFA code for " + username + ": " + code);
    }

    @Override
    public void verify(String username, String payload, String credential) {
        Code issued = codes.get(username);
        if (issued == null || !issued.expiresAt().isAfter(Instant.now()) || !issued.value().equals(credential)
                || !codes.remove(username, issued)) {
            throw new BadCredentialsException("Invalid or expired email code");
        }
    }

    @Override
    public BindingMaterial beginBinding(String username) {
        prepare(username, null);
        return new BindingMaterial(ID, "");
    }

    @Override
    public String completeBinding(String username, BindingMaterial material, String confirmation) {
        verify(username, material.secret(), confirmation);
        return "bound";
    }

    private record Code(String value, Instant expiresAt) {
    }

}
