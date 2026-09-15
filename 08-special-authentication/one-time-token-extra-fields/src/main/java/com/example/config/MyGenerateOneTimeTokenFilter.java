package com.example.config;

import java.io.IOException;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.ott.DefaultGenerateOneTimeTokenRequestResolver;
import org.springframework.security.web.authentication.ott.GenerateOneTimeTokenRequestResolver;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyGenerateOneTimeTokenFilter extends OncePerRequestFilter {

    public static final String DEFAULT_GENERATE_URL = "/ott/generate";
    private final OneTimeTokenService tokenService;
    private final OneTimeTokenGenerationSuccessHandler tokenGenerationSuccessHandler;
    private final CustomUserDetailsService customUserDetailsService;
    private RequestMatcher requestMatcher = PathPatternRequestMatcher.withDefaults()
            .matcher(HttpMethod.POST, DEFAULT_GENERATE_URL);

    private GenerateOneTimeTokenRequestResolver requestResolver = new DefaultGenerateOneTimeTokenRequestResolver();

    public MyGenerateOneTimeTokenFilter(OneTimeTokenService tokenService,
                                        OneTimeTokenGenerationSuccessHandler tokenGenerationSuccessHandler,
                                        CustomUserDetailsService customUserDetailsService) {
        Assert.notNull(tokenService, "tokenService cannot be null");
        Assert.notNull(tokenGenerationSuccessHandler, "tokenGenerationSuccessHandler cannot be null");
        this.tokenService = tokenService;
        this.tokenGenerationSuccessHandler = tokenGenerationSuccessHandler;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String userName = request.getParameter("username");
        String password = request.getParameter("password");

        if (!this.requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        GenerateOneTimeTokenRequest generateRequest = this.requestResolver.resolve(request);
        if (generateRequest == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            UserDetails user = customUserDetailsService.loadUserByUsername(userName);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(password, user.getPassword())) {
                throw new Exception();
            }
        } catch (Exception e) {
            userName = "Anonymous";
        }
        IO.println("Username: " + userName + " - " + password);
        generateRequest = new GenerateOneTimeTokenRequest(userName);
        OneTimeToken ott = this.tokenService.generate(generateRequest);
        this.tokenGenerationSuccessHandler.handle(request, response, ott);

    }

    public void setRequestMatcher(RequestMatcher requestMatcher) {
        Assert.notNull(requestMatcher, "requestMatcher cannot be null");
        this.requestMatcher = requestMatcher;
    }

    public void setRequestResolver(GenerateOneTimeTokenRequestResolver requestResolver) {
        Assert.notNull(requestResolver, "requestResolver cannot be null");
        this.requestResolver = requestResolver;
    }

}
