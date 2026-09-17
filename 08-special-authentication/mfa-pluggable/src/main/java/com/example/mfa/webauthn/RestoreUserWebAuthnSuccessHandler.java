package com.example.mfa.webauthn;

import com.example.user.User;
import com.example.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpMessageConverterAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestoreUserWebAuthnSuccessHandler implements AuthenticationSuccessHandler {

	private final UserService userService;

	private final AuthenticationSuccessHandler delegate;

	private final SecurityContextRepository contextRepository = new HttpSessionSecurityContextRepository();

	public RestoreUserWebAuthnSuccessHandler(UserService userService) {
		this.userService = userService;
		HttpMessageConverterAuthenticationSuccessHandler successHandler = new HttpMessageConverterAuthenticationSuccessHandler();
		successHandler.setRequestCache(new NullRequestCache());
		this.delegate = successHandler;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		User user = this.userService.findByUsername(authentication.getName());
		Authentication restored = UsernamePasswordAuthenticationToken
			.authenticated(user, null, authentication.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(restored);
		SecurityContextHolder.setContext(context);
		this.contextRepository.saveContext(context, request, response);
		this.delegate.onAuthenticationSuccess(request, response, restored);
	}

}
