package com.example.mfa.mnemonic;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Component
public class Bip39 {

	private static final int ENTROPY_BYTES = 16;

	private static final List<String> WORDS = loadWords();

	private final SecureRandom random = new SecureRandom();

	public String generate() {
		byte[] entropy = new byte[ENTROPY_BYTES];
		this.random.nextBytes(entropy);
		return toMnemonic(entropy);
	}

	String toMnemonic(byte[] entropy) {
		if (entropy.length != ENTROPY_BYTES) {
			throw new IllegalArgumentException("entropy must be 16 bytes");
		}
		int acc = 0;
		int bits = 0;
		StringBuilder out = new StringBuilder();
		for (byte b : entropy) {
			acc = (acc << 8) | (b & 0xff);
			bits += 8;
			while (bits >= 11) {
				bits -= 11;
				append(out, (acc >>> bits) & 0x7ff);
				acc &= (1 << bits) - 1;
			}
		}
		append(out, (acc << 4 | checksumNibble(entropy)) & 0x7ff);
		return out.toString();
	}

	public String normalize(String phrase) {
		return phrase.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
	}

	public boolean checksumValid(String phrase) {
		String[] parts = normalize(phrase).split(" ");
		if (parts.length != 12) {
			return false;
		}
		int acc = 0;
		int bits = 0;
		byte[] entropy = new byte[ENTROPY_BYTES];
		int written = 0;
		for (String part : parts) {
			int index = Collections.binarySearch(WORDS, part);
			if (index < 0) {
				return false;
			}
			acc = (acc << 11) | index;
			bits += 11;
			while (bits >= 8 && written < ENTROPY_BYTES) {
				bits -= 8;
				entropy[written++] = (byte) ((acc >>> bits) & 0xff);
				acc &= (1 << bits) - 1;
			}
		}
		return bits == 4 && acc == checksumNibble(entropy);
	}

	private static void append(StringBuilder out, int index) {
		if (!out.isEmpty()) {
			out.append(' ');
		}
		out.append(WORDS.get(index));
	}

	private static int checksumNibble(byte[] entropy) {
		return (sha256(entropy)[0] & 0xff) >>> 4;
	}

	private static byte[] sha256(byte[] data) {
		try {
			return MessageDigest.getInstance("SHA-256").digest(data);
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	private static List<String> loadWords() {
		try (InputStream in = Bip39.class.getResourceAsStream("/bip39/english.txt")) {
			if (in == null) {
				throw new IllegalStateException("missing /bip39/english.txt");
			}
			List<String> words = new String(in.readAllBytes(), StandardCharsets.UTF_8).lines()
				.filter(line -> !line.isBlank())
				.toList();
			if (words.size() != 2048) {
				throw new IllegalStateException("expected 2048 words, got " + words.size());
			}
			return words;
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
