package com.example.mfa;

import com.example.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class MfaFactors {

	private MfaFactors() {
	}

	public static Authentication grant(Authentication authentication, User principal, MfaProvider provider) {
		return grant(authentication, principal, provider.authority());
	}

	public static Authentication grant(Authentication authentication, User principal, String authority) {
		Authentication token = authentication.toBuilder()
			.principal(principal)
			.authorities(authorities -> authorities.add(FactorGrantedAuthority.fromAuthority(authority)))
			.build();
		SecurityContextHolder.getContext().setAuthentication(token);
		return token;
	}

	public static boolean granted(Authentication authentication, String authority) {
		return authentication.getAuthorities().stream().anyMatch(a -> authority.equals(a.getAuthority()));
	}

}
