package com.example.mfa;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class TotpMfaProvider implements MfaProvider {

    public static final String ID = "totp";

    private final TotpSupport totpSupport;

    public TotpMfaProvider(TotpSupport totpSupport) {
        this.totpSupport = totpSupport;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String displayName() {
        return "Authenticator app (TOTP)";
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
    public void verify(String username, String payload, String credential) {
        totpSupport.verify(payload, credential);
    }

    @Override
    public BindingMaterial beginBinding(String username) {
        String secret = totpSupport.generateSecret();
        return new BindingMaterial(ID, secret, Map.of(
                "otpAuthUri", totpSupport.otpAuthUri(username, secret)));
    }

    @Override
    public String completeBinding(String username, BindingMaterial material, String confirmation) {
        totpSupport.verify(material.secret(), confirmation);
        return material.secret();
    }

}
