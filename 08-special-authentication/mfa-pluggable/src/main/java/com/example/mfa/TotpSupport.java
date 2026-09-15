package com.example.mfa;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
public class TotpSupport {

    private static final int PERIOD_SECONDS = 30;
    private static final int DIGITS = 6;
    private static final int WINDOW = 1;
    private static final String BASE32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    public TotpSupport() {
        this(Clock.systemUTC());
    }

    TotpSupport(Clock clock) {
        this.clock = clock;
    }

    public String generateSecret() {
        byte[] bytes = new byte[10];
        random.nextBytes(bytes);
        return encodeBase32(bytes);
    }

    public String otpAuthUri(String username, String secret) {
        return "otpauth://totp/mfa-pluggable:" + username
                + "?secret=" + secret
                + "&issuer=mfa-pluggable"
                + "&algorithm=SHA1&digits=6&period=30";
    }

    public String currentCode(String secret) {
        return generate(secret, clock.instant());
    }

    public void verify(String secret, String code) {
        if (code == null || !code.matches("\\d{" + DIGITS + "}")) {
            throw new BadCredentialsException("Invalid TOTP");
        }
        byte[] key = decodeBase32(secret);
        long counter = clock.instant().getEpochSecond() / PERIOD_SECONDS;
        byte[] expected = code.getBytes(StandardCharsets.US_ASCII);
        for (int offset = -WINDOW; offset <= WINDOW; offset++) {
            byte[] candidate = generate(key, counter + offset).getBytes(StandardCharsets.US_ASCII);
            if (MessageDigest.isEqual(expected, candidate)) {
                return;
            }
        }
        throw new BadCredentialsException("Invalid TOTP");
    }

    private String generate(String base32Secret, Instant instant) {
        return generate(decodeBase32(base32Secret), instant.getEpochSecond() / PERIOD_SECONDS);
    }

    private String generate(byte[] key, long counter) {
        try {
            byte[] data = ByteBuffer.allocate(8).putLong(counter).array();
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(data);
            int offset = hash[hash.length - 1] & 0x0f;
            int binary = ((hash[offset] & 0x7f) << 24)
                    | ((hash[offset + 1] & 0xff) << 16)
                    | ((hash[offset + 2] & 0xff) << 8)
                    | (hash[offset + 3] & 0xff);
            return ("%0" + DIGITS + "d").formatted(binary % 1_000_000);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Unable to generate TOTP", ex);
        }
    }

    static String encodeBase32(byte[] data) {
        StringBuilder out = new StringBuilder((data.length * 8 + 4) / 5);
        int buffer = 0;
        int bits = 0;
        for (byte value : data) {
            buffer = (buffer << 8) | (value & 0xff);
            bits += 8;
            while (bits >= 5) {
                bits -= 5;
                out.append(BASE32.charAt((buffer >>> bits) & 0x1f));
            }
        }
        if (bits > 0) {
            out.append(BASE32.charAt((buffer << (5 - bits)) & 0x1f));
        }
        return out.toString();
    }

    static byte[] decodeBase32(String value) {
        String normalized = value.toUpperCase(Locale.ROOT).replace("=", "").replace(" ", "");
        int buffer = 0;
        int bits = 0;
        int length = (normalized.length() * 5) / 8;
        byte[] output = new byte[length];
        int index = 0;
        for (int i = 0; i < normalized.length(); i++) {
            int nibble = BASE32.indexOf(normalized.charAt(i));
            if (nibble < 0) {
                throw new BadCredentialsException("Invalid TOTP secret");
            }
            buffer = (buffer << 5) | nibble;
            bits += 5;
            if (bits >= 8) {
                bits -= 8;
                output[index++] = (byte) ((buffer >>> bits) & 0xff);
            }
        }
        return output;
    }

}
