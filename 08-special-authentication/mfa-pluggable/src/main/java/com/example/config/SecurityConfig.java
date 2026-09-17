package com.example.config;

import com.example.mfa.mnemonic.MnemonicMfaProvider;
import com.example.mfa.email.EmailMfaProvider;
import com.example.mfa.totp.TotpMfaProvider;
import com.example.mfa.webauthn.RestoreUserWebAuthnSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.authorization.DefaultAuthorizationManagerFactory;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.ui.DefaultResourcesFilter;
import org.springframework.security.web.webauthn.authentication.WebAuthnAuthenticationFilter;
import org.springframework.security.web.webauthn.management.JdbcPublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.JdbcUserCredentialRepository;

@Configuration(proxyBeanMethods = false)
@EnableMultiFactorAuthentication(authorities = {})
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, MfaAuthorizationManager mfaAuthorizationManager,
			RestoreUserWebAuthnSuccessHandler webAuthnSuccessHandler) throws Exception {
		DefaultAuthorizationManagerFactory<RequestAuthorizationContext> mfa = new DefaultAuthorizationManagerFactory<>();
		mfa.setAdditionalAuthorization(mfaAuthorizationManager);

		AuthenticationEntryPoint challenge = (request, response, exception) -> response.sendRedirect("/challenge");

		return http.authorizeHttpRequests(authorize -> authorize
		// @formatter:off
			.requestMatchers("/error", "/signup", "/*.css", "/*.js", "/login/webauthn.js").permitAll()
			.requestMatchers("/challenge", "/enable-mfa", "/enable-email", "/enable-mnemonic",
					"/enable-mnemonic/**", "/enable-webauthn", "/webauthn/**")
				.hasAuthority(FactorGrantedAuthority.PASSWORD_AUTHORITY)
			.anyRequest().access(mfa.authenticated())
		// @formatter:on
		)
			.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
			.webAuthn(webAuthn -> webAuthn.rpName("Demo MFA")
				.rpId("localhost")
				.allowedOrigins("http://localhost:8080")
				.disableDefaultRegistrationPage(true)
				.withObjectPostProcessor(new ObjectPostProcessor<WebAuthnAuthenticationFilter>() {
					@Override
					public <O extends WebAuthnAuthenticationFilter> O postProcess(O filter) {
						filter.setMfaEnabled(true);
						filter.setAuthenticationSuccessHandler(webAuthnSuccessHandler);
						return filter;
					}
				}))
			.exceptionHandling(exceptions -> exceptions
				.defaultDeniedHandlerForMissingAuthority(challenge, TotpMfaProvider.TOTP_AUTHORITY)
				.defaultDeniedHandlerForMissingAuthority(challenge, EmailMfaProvider.EMAIL_AUTHORITY)
				.defaultDeniedHandlerForMissingAuthority(challenge, MnemonicMfaProvider.MNEMONIC_AUTHORITY)
				.defaultDeniedHandlerForMissingAuthority(challenge, FactorGrantedAuthority.WEBAUTHN_AUTHORITY))
			.securityContext(securityContext -> securityContext.requireExplicitSave(false))
			.addFilter(DefaultResourcesFilter.webauthn())
			.build();
	}

	@Bean
	JdbcPublicKeyCredentialUserEntityRepository jdbcPublicKeyCredentialUserEntityRepository(JdbcOperations jdbc) {
		return new JdbcPublicKeyCredentialUserEntityRepository(jdbc);
	}

	@Bean
	JdbcUserCredentialRepository jdbcUserCredentialRepository(JdbcOperations jdbc) {
		return new JdbcUserCredentialRepository(jdbc);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

}
