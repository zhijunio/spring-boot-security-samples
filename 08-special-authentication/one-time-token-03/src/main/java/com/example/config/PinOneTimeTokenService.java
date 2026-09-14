package com.example.config;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.ott.DefaultOneTimeToken;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class PinOneTimeTokenService implements OneTimeTokenService {

    private static final int PIN_LENGTH = 5;
    private static final int MAX_PIN_VALUE = 100_000;

    private final Map<String, OneTimeToken> oneTimeTokenByToken = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    private Clock clock = Clock.systemUTC();
    private Duration tokenExpiresIn = Duration.ofMinutes(5);

    @Override
    public OneTimeToken generate(GenerateOneTimeTokenRequest request) {
        String token = generatePin();
        Instant expiresAt = this.clock.instant().plus(this.tokenExpiresIn);
        OneTimeToken ott = new DefaultOneTimeToken(token, request.getUsername(), expiresAt);
        this.oneTimeTokenByToken.put(token, ott);
        cleanExpiredTokensIfNeeded();
        return ott;
    }

    @Override
    public @Nullable OneTimeToken consume(OneTimeTokenAuthenticationToken authenticationToken) {
        OneTimeToken ott = this.oneTimeTokenByToken.remove(authenticationToken.getTokenValue());
        if (ott == null || isExpired(ott)) {
            return null;
        }
        return ott;
    }

    public void setTokenExpiresIn(Duration tokenExpiresIn) {
        Assert.notNull(tokenExpiresIn, "tokenExpiresIn cannot be null");
        Assert.isTrue(!tokenExpiresIn.isNegative() && !tokenExpiresIn.isZero(),
                "tokenExpiresIn must be positive");
        this.tokenExpiresIn = tokenExpiresIn;
    }

    private String generatePin() {
        int pin = secureRandom.nextInt(MAX_PIN_VALUE);
        return String.format("%0" + PIN_LENGTH + "d", pin);
    }

    private void cleanExpiredTokensIfNeeded() {
        if (this.oneTimeTokenByToken.size() < 100) {
            return;
        }
        for (Map.Entry<String, OneTimeToken> entry : this.oneTimeTokenByToken.entrySet()) {
            if (isExpired(entry.getValue())) {
                this.oneTimeTokenByToken.remove(entry.getKey());
            }
        }
    }

    private boolean isExpired(OneTimeToken ott) {
        return this.clock.instant().isAfter(ott.getExpiresAt());
    }

    public void setClock(Clock clock) {
        Assert.notNull(clock, "clock cannot be null");
        this.clock = clock;
    }

}
