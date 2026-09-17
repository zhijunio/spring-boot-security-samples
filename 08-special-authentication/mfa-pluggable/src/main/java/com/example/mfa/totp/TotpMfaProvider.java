package com.example.mfa.totp;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.example.mfa.MfaProvider;
import com.example.user.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

@Component
public class TotpMfaProvider implements MfaProvider {

	public static final String TOTP_AUTHORITY = "FACTOR_TOTP";

	private static final int SECRET_BYTES = 20;

	private final TimeBasedOneTimePasswordGenerator generator = new TimeBasedOneTimePasswordGenerator();

	private final SecureRandom random = new SecureRandom();

	public String generateSecret() {
		byte[] bytes = new byte[SECRET_BYTES];
		this.random.nextBytes(bytes);
		return Base32.encode(bytes);
	}

	@Override
	public String authority() {
		return TOTP_AUTHORITY;
	}

	@Override
	public String method() {
		return "totp";
	}

	@Override
	public String label() {
		return "Authenticator";
	}

	@Override
	public boolean enabled(User user) {
		return user.totpMfaEnabled();
	}

	@Override
	public boolean verify(User user, String code) {
		return verify(user.totpSecret(), code);
	}

	public boolean verify(String secret, String code) {
		if (!StringUtils.hasText(code) || !code.chars().allMatch(Character::isDigit)) {
			return false;
		}
		try {
			SecretKey key = toKey(secret);
			int expected = Integer.parseInt(code);
			Instant now = Instant.now();
			Duration step = this.generator.getTimeStep();
			for (int i = -1; i <= 1; i++) {
				if (this.generator.generateOneTimePassword(key, now.plus(step.multipliedBy(i))) == expected) {
					return true;
				}
			}
			return false;
		}
		catch (InvalidKeyException | IllegalArgumentException e) {
			return false;
		}
	}

	int codeAt(String secret, Instant instant) throws InvalidKeyException {
		return this.generator.generateOneTimePassword(toKey(secret), instant);
	}

	private SecretKey toKey(String secret) {
		byte[] decoded = Base32.decode(secret);
		return new SecretKeySpec(decoded, this.generator.getAlgorithm());
	}

}
