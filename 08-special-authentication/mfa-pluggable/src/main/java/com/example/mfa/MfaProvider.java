package com.example.mfa;

/**
 * Pluggable second-factor provider. New methods = new Spring bean implementing this SPI.
 */
public interface MfaProvider {

    String id();

    String displayName();

    /**
     * Optional side effect before the user submits a credential (e.g. send email code).
     */
    default void prepare(String username, String payload) {
    }

    default boolean needsPrepare() {
        return false;
    }

    default boolean needsBindingConfirmation() {
        return false;
    }

    default String credentialPlaceholder() {
        return "code";
    }

    /**
     * Verify the submitted credential against the stored payload.
     * @throws org.springframework.security.authentication.BadCredentialsException on failure
     */
    void verify(String username, String payload, String credential);

    /**
     * Start binding; return material the UI should show (secret, mnemonic, …).
     */
    BindingMaterial beginBinding(String username);

    /**
     * Persist the binding after optional confirmation (e.g. first TOTP code).
     * @return payload stored for later {@link #verify}
     */
    String completeBinding(String username, BindingMaterial material, String confirmation);

}
