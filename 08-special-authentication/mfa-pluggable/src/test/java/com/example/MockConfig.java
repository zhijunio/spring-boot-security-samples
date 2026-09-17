package com.example;

import com.example.mfa.email.EmailMfaProvider;
import com.example.mfa.totp.TotpMfaProvider;
import com.example.user.User;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration(proxyBeanMethods = false)
public class MockConfig {

	@Primary
	@Bean
	TotpMfaProvider mockTotpMfaProvider() {
		return new TotpMfaProvider() {
			@Override
			public boolean verify(User user, String code) {
				return "1234".equals(code);
			}
		};
	}

	@Primary
	@Bean
	EmailMfaProvider mockEmailMfaProvider(JdbcClient jdbcClient, PasswordEncoder passwordEncoder) {
		return new EmailMfaProvider(jdbcClient, passwordEncoder) {
			@Override
			public void sendChallenge(User user) {
			}

			@Override
			public boolean verify(User user, String code) {
				return "1234".equals(code);
			}
		};
	}

}
