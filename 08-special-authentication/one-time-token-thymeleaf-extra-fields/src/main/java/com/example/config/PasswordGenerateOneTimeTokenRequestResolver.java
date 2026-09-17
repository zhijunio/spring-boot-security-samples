package com.example.config;

import com.example.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.ott.GenerateOneTimeTokenRequestResolver;
import org.springframework.util.StringUtils;

final class PasswordGenerateOneTimeTokenRequestResolver implements GenerateOneTimeTokenRequestResolver {

    private final UserService users;

    private final PasswordEncoder passwordEncoder;

    PasswordGenerateOneTimeTokenRequestResolver(UserService users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public GenerateOneTimeTokenRequest resolve(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return null;
        }
        try {
            UserDetails user = this.users.loadUserByUsername(username);
            if (!this.passwordEncoder.matches(password, user.getPassword())) {
                return null;
            }
            return new GenerateOneTimeTokenRequest(username);
        }
        catch (UsernameNotFoundException ex) {
            return null;
        }
    }

}
