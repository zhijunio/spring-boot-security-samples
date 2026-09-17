package com.example.mfa.mnemonic;

import com.example.mfa.MfaProvider;
import com.example.user.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class MnemonicMfaProvider implements MfaProvider {

	public static final String MNEMONIC_AUTHORITY = "FACTOR_MNEMONIC";

	private final Bip39 bip39;

	public MnemonicMfaProvider(Bip39 bip39) {
		this.bip39 = bip39;
	}

	@Override
	public String authority() {
		return MNEMONIC_AUTHORITY;
	}

	@Override
	public String method() {
		return "mnemonic";
	}

	@Override
	public String label() {
		return "Mnemonic";
	}

	@Override
	public boolean enabled(User user) {
		return user.mnemonicMfaEnabled();
	}

	public String generate() {
		return this.bip39.generate();
	}

	public String hash(String phrase) {
		try {
			byte[] hash = MessageDigest.getInstance("SHA-256")
				.digest(this.bip39.normalize(phrase).getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hash);
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	public boolean matches(String expected, String typed) {
		return this.bip39.normalize(expected).equals(this.bip39.normalize(typed)) && this.bip39.checksumValid(typed);
	}

	@Override
	public boolean verify(User user, String code) {
		if (!StringUtils.hasText(code) || !StringUtils.hasText(user.mnemonicPhraseHash())) {
			return false;
		}
		if (!this.bip39.checksumValid(code)) {
			return false;
		}
		byte[] expected = user.mnemonicPhraseHash().getBytes(StandardCharsets.UTF_8);
		byte[] actual = hash(code).getBytes(StandardCharsets.UTF_8);
		return MessageDigest.isEqual(expected, actual);
	}

}
