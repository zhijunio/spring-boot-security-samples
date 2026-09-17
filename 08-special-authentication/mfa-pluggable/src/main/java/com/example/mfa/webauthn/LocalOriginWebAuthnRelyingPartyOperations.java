package com.example.mfa.webauthn;

import org.springframework.security.web.webauthn.api.CredentialRecord;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialCreationOptions;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialRequestOptions;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialRpEntity;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialCreationOptionsRequest;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialRequestOptionsRequest;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.RelyingPartyAuthenticationRequest;
import org.springframework.security.web.webauthn.management.RelyingPartyRegistrationRequest;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.security.web.webauthn.management.WebAuthnRelyingPartyOperations;
import org.springframework.security.web.webauthn.management.Webauthn4JRelyingPartyOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocalOriginWebAuthnRelyingPartyOperations implements WebAuthnRelyingPartyOperations {

	private final PublicKeyCredentialUserEntityRepository userEntities;

	private final UserCredentialRepository userCredentials;

	private final ConcurrentHashMap<String, Webauthn4JRelyingPartyOperations> delegates = new ConcurrentHashMap<>();

	public LocalOriginWebAuthnRelyingPartyOperations(PublicKeyCredentialUserEntityRepository userEntities,
			UserCredentialRepository userCredentials) {
		this.userEntities = userEntities;
		this.userCredentials = userCredentials;
	}

	@Override
	public PublicKeyCredentialCreationOptions createPublicKeyCredentialCreationOptions(
			PublicKeyCredentialCreationOptionsRequest request) {
		return delegate().createPublicKeyCredentialCreationOptions(request);
	}

	@Override
	public CredentialRecord registerCredential(RelyingPartyRegistrationRequest relyingPartyRegistrationRequest) {
		return delegate().registerCredential(relyingPartyRegistrationRequest);
	}

	@Override
	public PublicKeyCredentialRequestOptions createCredentialRequestOptions(
			PublicKeyCredentialRequestOptionsRequest request) {
		return delegate().createCredentialRequestOptions(request);
	}

	@Override
	public PublicKeyCredentialUserEntity authenticate(RelyingPartyAuthenticationRequest request) {
		return delegate().authenticate(request);
	}

	private Webauthn4JRelyingPartyOperations delegate() {
		return this.delegates.computeIfAbsent(origin(), origin -> new Webauthn4JRelyingPartyOperations(this.userEntities,
				this.userCredentials, PublicKeyCredentialRpEntity.builder().id("localhost").name("Demo MFA").build(),
				Set.of(origin)));
	}

	private static String origin() {
		if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
			HttpServletRequest request = attrs.getRequest();
			String header = request.getHeader("Origin");
			if (StringUtils.hasText(header)) {
				return header;
			}
			int port = request.getServerPort();
			String base = request.getScheme() + "://" + request.getServerName();
			boolean defaultPort = ("http".equals(request.getScheme()) && port == 80)
					|| ("https".equals(request.getScheme()) && port == 443);
			return defaultPort ? base : base + ":" + port;
		}
		return "http://localhost:8080";
	}

}
