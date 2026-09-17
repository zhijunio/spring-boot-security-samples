package com.example.mfa.totp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Base32Tests {

	@Test
	void roundTrip() {
		byte[] data = { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef, 0x01, 0x23, 0x45,
				0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef, 0x01, 0x23, 0x45, 0x67 };
		assertThat(Base32.decode(Base32.encode(data))).isEqualTo(data);
	}

	@Test
	void decodeIgnoresPaddingAndCase() {
		assertThat(Base32.decode("mfzwizltoq======")).isEqualTo(Base32.decode("MFZWIZLTOQ"));
	}

	@Test
	void decodeRejectsInvalidCharacter() {
		assertThatThrownBy(() -> Base32.decode("MFZW1")).isInstanceOf(IllegalArgumentException.class);
	}

}
