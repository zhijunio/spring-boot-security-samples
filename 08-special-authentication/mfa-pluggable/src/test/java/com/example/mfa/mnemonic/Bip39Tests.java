package com.example.mfa.mnemonic;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Bip39Tests {

	private final Bip39 bip39 = new Bip39();

	@Test
	void zeroEntropyIsCanonicalTwelveWords() {
		assertThat(this.bip39.toMnemonic(new byte[16]))
			.isEqualTo("abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about");
	}

	@Test
	void generateHasValidChecksum() {
		assertThat(this.bip39.checksumValid(this.bip39.generate())).isTrue();
	}

	@Test
	void normalizeCollapsesWhitespace() {
		assertThat(this.bip39.normalize("  Abandon   ABOUT  ")).isEqualTo("abandon about");
	}

	@Test
	void checksumRejectsWrongWordCount() {
		assertThat(this.bip39.checksumValid("abandon about")).isFalse();
	}

	@Test
	void checksumRejectsUnknownWord() {
		assertThat(this.bip39.checksumValid(
				"abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon notaword"))
			.isFalse();
	}

}
