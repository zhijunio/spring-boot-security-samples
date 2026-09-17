package com.example.config;

import com.example.mfa.MfaProvider;
import com.example.mfa.PrimaryMfa;
import com.example.mfa.mnemonic.MnemonicMfaProvider;
import com.example.user.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AllAuthoritiesAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Component
public class MfaAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

	private final PrimaryMfa primaries;

	private final MnemonicMfaProvider mnemonic;

	public MfaAuthorizationManager(PrimaryMfa primaries, MnemonicMfaProvider mnemonic) {
		this.primaries = primaries;
		this.mnemonic = mnemonic;
	}

	@Override
	public AuthorizationResult authorize(Supplier<? extends @Nullable Authentication> authentication,
			RequestAuthorizationContext context) {
		if (authentication.get() instanceof UsernamePasswordAuthenticationToken token
				&& token.getPrincipal() instanceof User user) {
			List<String> required = new ArrayList<>();
			required.add(FactorGrantedAuthority.PASSWORD_AUTHORITY);
			if (this.mnemonic.verified(token, user)) {
				required.add(this.mnemonic.authority());
			}
			else if (this.primaries.anyEnabled(user)) {
				this.primaries.factors()
					.stream()
					.filter(provider -> provider.enabled(user))
					.map(MfaProvider::authority)
					.forEach(required::add);
			}
			else if (this.mnemonic.enabled(user)) {
				required.add(this.mnemonic.authority());
			}
			return AllAuthoritiesAuthorizationManager.hasAllAuthorities(required.toArray(String[]::new))
				.authorize(authentication, context);
		}
		return new AuthorizationDecision(false);
	}

}
