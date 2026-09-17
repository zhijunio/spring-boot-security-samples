package com.example.mfa.email;

import com.example.TestcontainersConfiguration;
import com.example.user.User;
import com.example.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class EmailMfaProviderTests {

	@Autowired
	EmailMfaProvider emailMfa;

	@Autowired
	UserService userService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	JdbcClient jdbcClient;

	@Test
	void sendChallengeInsertsTimestamp() {
		String username = "email-" + UUID.randomUUID().toString().substring(0, 8);
		User user = User.registered(username, this.passwordEncoder.encode("secret"), username + "@example.test",
				"totp-secret");
		this.userService.insert(user);

		this.emailMfa.sendChallenge(user);

		Integer count = this.jdbcClient.sql("SELECT COUNT(*) FROM email_mfa_codes WHERE username = ?")
			.param(username)
			.query(Integer.class)
			.single();
		assertThat(count).isEqualTo(1);
	}

	@Test
	void cooldownBlocksImmediateResend() {
		String username = "email-" + UUID.randomUUID().toString().substring(0, 8);
		User user = User.registered(username, this.passwordEncoder.encode("secret"), username + "@example.test",
				"totp-secret");
		this.userService.insert(user);

		this.emailMfa.sendChallenge(user);
		assertThat(this.emailMfa.cooldownRemaining(user)).isBetween(1, 60);
		assertThat(this.emailMfa.sendChallengeIfReady(user)).isFalse();
	}

}
