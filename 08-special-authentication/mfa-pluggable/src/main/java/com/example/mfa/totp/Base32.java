package com.example.mfa.totp;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

final class Base32 {

	private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();

	private static final int[] INDEX = new int[128];

	static {
		Arrays.fill(INDEX, -1);
		for (int i = 0; i < ALPHABET.length; i++) {
			INDEX[ALPHABET[i]] = i;
			if (ALPHABET[i] >= 'A' && ALPHABET[i] <= 'Z') {
				INDEX[Character.toLowerCase(ALPHABET[i])] = i;
			}
		}
	}

	private Base32() {
	}

	static String encode(byte[] data) {
		if (data.length == 0) {
			return "";
		}
		StringBuilder out = new StringBuilder((data.length * 8 + 4) / 5);
		int buffer = 0;
		int bitsLeft = 0;
		for (byte b : data) {
			buffer = (buffer << 8) | (b & 0xff);
			bitsLeft += 8;
			while (bitsLeft >= 5) {
				bitsLeft -= 5;
				out.append(ALPHABET[(buffer >>> bitsLeft) & 31]);
			}
		}
		if (bitsLeft > 0) {
			out.append(ALPHABET[(buffer << (5 - bitsLeft)) & 31]);
		}
		return out.toString();
	}

	static byte[] decode(String encoded) {
		String s = encoded.replace("=", "").replace(" ", "");
		if (s.isEmpty()) {
			return new byte[0];
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream(s.length() * 5 / 8);
		int buffer = 0;
		int bitsLeft = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			int value = c < INDEX.length ? INDEX[c] : -1;
			if (value < 0) {
				throw new IllegalArgumentException("Invalid Base32 character: " + c);
			}
			buffer = (buffer << 5) | value;
			bitsLeft += 5;
			if (bitsLeft >= 8) {
				bitsLeft -= 8;
				out.write((buffer >>> bitsLeft) & 0xff);
			}
		}
		return out.toByteArray();
	}

}
