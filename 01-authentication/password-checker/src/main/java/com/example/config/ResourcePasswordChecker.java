package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

public class ResourcePasswordChecker implements CompromisedPasswordChecker {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Resource resource;

    public ResourcePasswordChecker(Resource resource) {
        this.resource = resource;
    }

    @Override
    public CompromisedPasswordDecision check(String password) {
        if (password == null) return new CompromisedPasswordDecision(false);
        try (Stream<String> lines = Files.lines(resource.getFile().toPath())) {
            return new CompromisedPasswordDecision(lines.anyMatch(password::equals));
        } catch (IOException ex) {
            logger.warn("Could not read password file", ex);
            return new CompromisedPasswordDecision(false);
        }
    }
}
