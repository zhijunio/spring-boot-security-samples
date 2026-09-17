package com.example.mfa.mnemonic;

import com.example.TestcontainersConfiguration;
import com.example.user.User;
import com.example.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class MnemonicMfaProviderTests {

	@Autowired
	MnemonicMfaProvider mnemonicMfa;

	@Autowired
	UserService userService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Test
	void generateIsTwelveWordPhrase() {
		String phrase = this.mnemonicMfa.generate();
		assertThat(phrase.split(" ")).hasSize(12);
		assertThat(this.mnemonicMfa.matches(phrase, phrase.toUpperCase())).isTrue();
	}

	@Test
	void verifyAcceptsNormalizedPhrase() {
		String username = "mnemonic-" + UUID.randomUUID().toString().substring(0, 8);
		User user = User.registered(username, this.passwordEncoder.encode("secret"), username + "@example.test",
				"totp-secret");
		this.userService.insert(user);
		user = this.userService.enableTotp(user);
		String phrase = this.mnemonicMfa.generate();
		user = this.userService.enableMnemonic(user, this.mnemonicMfa.hash(phrase));

		assertThat(this.mnemonicMfa.verify(user, phrase)).isTrue();
		assertThat(this.mnemonicMfa.verify(user, "  " + phrase.toUpperCase() + "  ")).isTrue();
		assertThat(this.mnemonicMfa.verify(user, "abandon about")).isFalse();
	}

}
