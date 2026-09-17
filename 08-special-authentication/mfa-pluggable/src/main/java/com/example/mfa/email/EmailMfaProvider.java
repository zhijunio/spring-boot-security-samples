package com.example.mfa.email;

import com.example.mfa.MfaProvider;
import com.example.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class EmailMfaProvider implements MfaProvider {

	public static final String EMAIL_AUTHORITY = "FACTOR_EMAIL";

	private static final Duration TTL = Duration.ofMinutes(5);

	private static final Duration RESEND_INTERVAL = Duration.ofSeconds(60);

	private static final Logger log = LoggerFactory.getLogger(EmailMfaProvider.class);

	private final JdbcClient jdbcClient;

	private final PasswordEncoder passwordEncoder;

	private final SecureRandom random = new SecureRandom();

	public EmailMfaProvider(JdbcClient jdbcClient, PasswordEncoder passwordEncoder) {
		this.jdbcClient = jdbcClient;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public String authority() {
		return EMAIL_AUTHORITY;
	}

	@Override
	public String method() {
		return "email";
	}

	@Override
	public String label() {
		return "Email code";
	}

	@Override
	public boolean enabled(User user) {
		return user.emailMfaEnabled();
	}

	@Override
	public void sendChallenge(User user) {
		String code = "%06d".formatted(this.random.nextInt(1_000_000));
		OffsetDateTime expiresAt = OffsetDateTime.now(ZoneOffset.UTC).plus(TTL);
		this.jdbcClient.sql("""
				INSERT INTO email_mfa_codes(username, code_hash, expires_at)
				VALUES (?, ?, ?) AS new
				ON DUPLICATE KEY UPDATE code_hash = new.code_hash, expires_at = new.expires_at
				""")
			.params(user.username(), this.passwordEncoder.encode(code), expiresAt)
			.update();
		log.info("Email MFA code for {}: {}", user.email(), code);
	}

	@Override
	public void sendChallengeIfAbsent(User user) {
		EmailCode stored = find(user.username());
		if (stored == null || Instant.now().isAfter(stored.expiresAt().toInstant())) {
			sendChallenge(user);
		}
	}

	@Override
	public int cooldownRemaining(User user) {
		EmailCode stored = find(user.username());
		if (stored == null) {
			return 0;
		}
		Instant sentAt = stored.expiresAt().toInstant().minus(TTL);
		long elapsed = Duration.between(sentAt, Instant.now()).getSeconds();
		if (elapsed >= RESEND_INTERVAL.toSeconds()) {
			return 0;
		}
		return (int) (RESEND_INTERVAL.toSeconds() - elapsed);
	}

	@Override
	public boolean sendChallengeIfReady(User user) {
		if (cooldownRemaining(user) > 0) {
			return false;
		}
		sendChallenge(user);
		return true;
	}

	@Override
	public boolean verify(User user, String code) {
		if (!StringUtils.hasText(code)) {
			return false;
		}
		EmailCode stored = find(user.username());
		if (stored == null || Instant.now().isAfter(stored.expiresAt().toInstant())) {
			return false;
		}
		if (!this.passwordEncoder.matches(code, stored.codeHash())) {
			return false;
		}
		this.jdbcClient.sql("DELETE FROM email_mfa_codes WHERE username = ?").param(user.username()).update();
		return true;
	}

	private EmailCode find(String username) {
		try {
			return this.jdbcClient.sql("SELECT code_hash, expires_at FROM email_mfa_codes WHERE username = ?")
				.param(username)
				.query(EmailCode.class)
				.single();
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private record EmailCode(String codeHash, OffsetDateTime expiresAt) {
	}

}
