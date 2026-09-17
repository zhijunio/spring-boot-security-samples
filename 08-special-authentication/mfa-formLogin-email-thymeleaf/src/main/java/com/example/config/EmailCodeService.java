package com.example.config;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.authentication.BadCredentialsException;

public class EmailCodeService {

    private final Map<String, Code> codes = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public String issue(String username) {
        String code = "%06d".formatted(random.nextInt(1_000_000));
        codes.put(username, new Code(code, Instant.now().plusSeconds(300)));
        return code;
    }

    public void consume(String username, String code) {
        Code issued = codes.get(username);
        if (issued == null || !issued.expiresAt().isAfter(Instant.now()) || !issued.value().equals(code)
                || !codes.remove(username, issued)) {
            throw new BadCredentialsException("Invalid or expired email code");
        }
    }

    private record Code(String value, Instant expiresAt) {
    }
}
