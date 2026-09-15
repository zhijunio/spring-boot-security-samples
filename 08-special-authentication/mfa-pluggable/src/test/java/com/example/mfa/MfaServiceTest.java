package com.example.mfa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

class MfaServiceTest {

    private TotpSupport totpSupport;
    private Bip39 bip39;
    private MfaBindingStore bindingStore;
    private MfaPlatformSettings platformSettings;
    private MfaService mfaService;

    @BeforeEach
    void setUp() {
        totpSupport = new TotpSupport();
        bip39 = new Bip39();
        List<MfaProvider> providers = List.of(
                new EmailMfaProvider(),
                new TotpMfaProvider(totpSupport),
                new Bip39MfaProvider(bip39));
        bindingStore = new MfaBindingStore();
        platformSettings = new MfaPlatformSettings(providers);
        mfaService = new MfaService(providers, bindingStore, platformSettings);
        bindingStore.bind("user", TotpMfaProvider.ID, "JBSWY3DPEHPK3PXP");
        bindingStore.bind("multi", TotpMfaProvider.ID, "JBSWY3DPEHPK3PXP");
        bindingStore.bind("multi", Bip39MfaProvider.ID,
                new Bip39MfaProvider(bip39).hashOf(
                        "payment noodle vivid slogan gather metal pilot enact fragile hip physical canvas"));
    }

    @Test
    void plainUserDoesNotRequireSecondFactor() {
        assertFalse(mfaService.requiresSecondFactor("plain"));
    }

    @Test
    void boundUserRequiresSecondFactor() {
        assertTrue(mfaService.requiresSecondFactor("user"));
    }

    @Test
    void disabledProviderDisappearsFromChallenge() {
        platformSettings.setEnabled(TotpMfaProvider.ID, false);
        assertTrue(mfaService.availableForChallenge("user").isEmpty());
        assertTrue(mfaService.requiresSecondFactor("user"));
    }

    @Test
    void multiUserCanVerifyWithEitherProvider() {
        mfaService.verify("multi", TotpMfaProvider.ID, totpSupport.currentCode("JBSWY3DPEHPK3PXP"));
        mfaService.verify("multi", Bip39MfaProvider.ID,
                "payment noodle vivid slogan gather metal pilot enact fragile hip physical canvas");
    }

    @Test
    void wrongTotpIsRejected() {
        assertThrows(BadCredentialsException.class,
                () -> mfaService.verify("user", TotpMfaProvider.ID, "000000"));
    }

    @Test
    void emailBindingRequiresCode() {
        EmailMfaProvider email = new EmailMfaProvider();
        BindingMaterial material = email.beginBinding("mailer");
        assertThrows(BadCredentialsException.class,
                () -> email.completeBinding("mailer", material, "000000"));
    }

    @Test
    void noopProviderCanBeRegistered() {
        MfaProvider noop = new MfaProvider() {
            @Override
            public String id() {
                return "noop";
            }

            @Override
            public String displayName() {
                return "Noop";
            }

            @Override
            public void verify(String username, String payload, String credential) {
                if (!"ok".equals(credential)) {
                    throw new BadCredentialsException("bad");
                }
            }

            @Override
            public BindingMaterial beginBinding(String username) {
                return new BindingMaterial(id(), "x");
            }

            @Override
            public String completeBinding(String username, BindingMaterial material, String confirmation) {
                return "x";
            }
        };
        List<MfaProvider> providers = List.of(noop);
        MfaBindingStore store = new MfaBindingStore();
        MfaPlatformSettings platform = new MfaPlatformSettings(providers);
        MfaService service = new MfaService(providers, store, platform);
        assertEquals(1, service.allProviders().size());
        assertEquals("noop", service.allProviders().getFirst().id());
    }

}
