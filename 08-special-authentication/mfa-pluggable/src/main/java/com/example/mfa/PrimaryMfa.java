package com.example.mfa;

import com.example.mfa.email.EmailMfaProvider;
import com.example.mfa.totp.TotpMfaProvider;
import com.example.mfa.webauthn.WebAuthnMfaProvider;
import com.example.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PrimaryMfa {

	private final List<MfaProvider> factors;

	public PrimaryMfa(TotpMfaProvider totp, EmailMfaProvider email, WebAuthnMfaProvider webAuthn) {
		this.factors = List.of(totp, email, webAuthn);
	}

	public List<MfaProvider> factors() {
		return this.factors;
	}

	public boolean anyEnabled(User user) {
		return this.factors.stream().anyMatch(provider -> provider.enabled(user));
	}

	public boolean anyVerified(Authentication authentication, User user) {
		return this.factors.stream().anyMatch(provider -> provider.verified(authentication, user));
	}

	public MfaProvider byMethod(String method) {
		return this.factors.stream().filter(provider -> provider.method().equals(method)).findFirst().orElse(null);
	}

}
