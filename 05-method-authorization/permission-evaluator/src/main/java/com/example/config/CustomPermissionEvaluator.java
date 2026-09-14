package com.example.config;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return targetDomainObject instanceof String target && permission instanceof String action
                && hasPermission(authentication, target, action);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                 String targetType, Object permission) {
        return permission instanceof String action && hasPermission(authentication, targetType + ":" + targetId, action);
    }

    private boolean hasPermission(Authentication authentication, String resource, String action) {
        return "read".equals(action) && resource.startsWith(authentication.getName() + ":");
    }
}
