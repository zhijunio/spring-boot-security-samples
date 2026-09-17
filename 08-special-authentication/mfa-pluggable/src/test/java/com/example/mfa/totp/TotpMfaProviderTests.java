package com.example.mfa.totp;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TotpMfaProviderTests {

	private final TotpMfaProvider totp = new TotpMfaProvider();

	@Test
	void verifyAcceptsCurrentCode() throws Exception {
		String secret = this.totp.generateSecret();
		String code = "%06d".formatted(this.totp.codeAt(secret, Instant.now()));
		assertThat(this.totp.verify(secret, code)).isTrue();
	}

	@Test
	void verifyRejectsWrongCode() {
		assertThat(this.totp.verify(this.totp.generateSecret(), "000000")).isFalse();
	}

}
