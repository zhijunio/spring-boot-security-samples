package com.example.mfa;

import com.example.user.User;
import org.springframework.security.core.Authentication;

public interface MfaProvider {

	String authority();

	String method();

	String label();

	boolean enabled(User user);

	boolean verify(User user, String code);

	default boolean pending(Authentication authentication, User user) {
		return enabled(user) && !MfaFactors.granted(authentication, authority());
	}

	default boolean verified(Authentication authentication, User user) {
		return enabled(user) && MfaFactors.granted(authentication, authority());
	}

	default void sendChallenge(User user) {
	}

	default void sendChallengeIfAbsent(User user) {
	}

	default int cooldownRemaining(User user) {
		return 0;
	}

	default boolean sendChallengeIfReady(User user) {
		sendChallenge(user);
		return true;
	}

}
