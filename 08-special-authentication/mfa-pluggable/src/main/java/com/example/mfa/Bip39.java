package com.example.mfa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

@Component
public final class Bip39 {

    public static final int WORD_COUNT = 12;
    public static final int ENTROPY_BITS = 128;

    private final List<String> words;
    private final Map<String, Integer> indexes;
    private final SecureRandom random = new SecureRandom();

    public Bip39() {
        this(loadEnglishWordlist());
    }

    Bip39(List<String> words) {
        if (words.size() != 2048) {
            throw new IllegalStateException("BIP-39 English wordlist must contain 2048 words");
        }
        this.words = List.copyOf(words);
        this.indexes = IntStream.range(0, words.size())
                .boxed()
                .collect(Collectors.toUnmodifiableMap(words::get, Function.identity()));
    }

    public String normalize(String mnemonic) {
        if (mnemonic == null || mnemonic.isBlank()) {
            return "";
        }
        return Arrays.stream(mnemonic.trim().toLowerCase(Locale.ROOT).split("\\s+"))
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining(" "));
    }

    public boolean isValid(String mnemonic) {
        try {
            validate(mnemonic);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public void validate(String mnemonic) {
        String normalized = normalize(mnemonic);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("BIP-39 mnemonic must be 12 words");
        }
        String[] parts = normalized.split(" ");
        if (parts.length != WORD_COUNT) {
            throw new IllegalArgumentException("BIP-39 mnemonic must be 12 words");
        }
        int[] indices = new int[WORD_COUNT];
        for (int i = 0; i < WORD_COUNT; i++) {
            Integer index = indexes.get(parts[i]);
            if (index == null) {
                throw new IllegalArgumentException("Word is not in the BIP-39 English wordlist");
            }
            indices[i] = index;
        }
        boolean[] bits = new boolean[WORD_COUNT * 11];
        int position = 0;
        for (int index : indices) {
            for (int bit = 10; bit >= 0; bit--) {
                bits[position++] = ((index >> bit) & 1) == 1;
            }
        }
        byte[] entropy = new byte[ENTROPY_BITS / 8];
        for (int i = 0; i < ENTROPY_BITS; i++) {
            if (bits[i]) {
                entropy[i / 8] |= (byte) (1 << (7 - (i % 8)));
            }
        }
        int checksumBits = ENTROPY_BITS / 32;
        int expectedChecksum = 0;
        for (int i = 0; i < checksumBits; i++) {
            expectedChecksum = (expectedChecksum << 1) | (bits[ENTROPY_BITS + i] ? 1 : 0);
        }
        int actualChecksum = (sha256(entropy)[0] & 0xff) >>> (8 - checksumBits);
        if (expectedChecksum != actualChecksum) {
            throw new IllegalArgumentException("BIP-39 checksum is invalid");
        }
    }

    public String generateMnemonic() {
        byte[] entropy = new byte[ENTROPY_BITS / 8];
        random.nextBytes(entropy);
        int checksumBits = ENTROPY_BITS / 32;
        int checksum = (sha256(entropy)[0] & 0xff) >>> (8 - checksumBits);
        boolean[] bits = new boolean[ENTROPY_BITS + checksumBits];
        for (int i = 0; i < ENTROPY_BITS; i++) {
            bits[i] = ((entropy[i / 8] >> (7 - (i % 8))) & 1) == 1;
        }
        for (int i = 0; i < checksumBits; i++) {
            bits[ENTROPY_BITS + i] = ((checksum >> (checksumBits - 1 - i)) & 1) == 1;
        }
        StringBuilder mnemonic = new StringBuilder();
        for (int i = 0; i < WORD_COUNT; i++) {
            int index = 0;
            for (int bit = 0; bit < 11; bit++) {
                index = (index << 1) | (bits[i * 11 + bit] ? 1 : 0);
            }
            if (i > 0) {
                mnemonic.append(' ');
            }
            mnemonic.append(words.get(index));
        }
        return mnemonic.toString();
    }

    List<String> words() {
        return words;
    }

    private static byte[] sha256(byte[] entropy) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(entropy);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static List<String> loadEnglishWordlist() {
        try (InputStream in = Bip39.class.getResourceAsStream("/bip39/english.txt")) {
            if (in == null) {
                throw new IllegalStateException("Missing BIP-39 English wordlist");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return reader.lines().map(String::trim).filter(line -> !line.isEmpty()).toList();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load BIP-39 English wordlist", ex);
        }
    }

}
