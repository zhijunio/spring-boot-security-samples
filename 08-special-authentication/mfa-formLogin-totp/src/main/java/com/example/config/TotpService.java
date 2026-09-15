package com.example.config;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.authentication.BadCredentialsException;

public class TotpService {

    public static final String USER_SECRET = "JBSWY3DPEHPK3PXP";
    public static final String ADMIN_SECRET = "KVKFKRCPNZQUYMLX";

    private static final int PERIOD_SECONDS = 30;
    private static final int DIGITS = 6;
    private static final int WINDOW = 1;
    private static final String BASE32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private final Map<String, String> secrets = Map.of(
            "user", USER_SECRET,
            "admin", ADMIN_SECRET);
    private final Clock clock;

    public TotpService() {
        this(Clock.systemUTC());
    }

    TotpService(Clock clock) {
        this.clock = clock;
    }

    public String secret(String username) {
        return requireSecret(username);
    }

    public String otpAuthUri(String username) {
        String secret = requireSecret(username);
        return "otpauth://totp/mfa-formLogin-totp:" + username
                + "?secret=" + secret
                + "&issuer=mfa-formLogin-totp"
                + "&algorithm=SHA1&digits=6&period=30";
    }

    public String currentCode(String username) {
        return generate(requireSecret(username), clock.instant());
    }

    public void verify(String username, String code) {
        if (code == null || !code.matches("\\d{" + DIGITS + "}")) {
            throw new BadCredentialsException("Invalid TOTP");
        }
        byte[] key = decodeBase32(requireSecret(username));
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

    private String requireSecret(String username) {
        String secret = secrets.get(username);
        if (secret == null) {
            throw new BadCredentialsException("Unknown TOTP user");
        }
        return secret;
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
