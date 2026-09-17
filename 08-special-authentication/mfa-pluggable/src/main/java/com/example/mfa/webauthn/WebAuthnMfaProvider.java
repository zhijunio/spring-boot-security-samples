package com.example.mfa.webauthn;

import com.example.mfa.MfaProvider;
import com.example.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.web.webauthn.api.AuthenticatorAssertionResponse;
import org.springframework.security.web.webauthn.api.PublicKeyCredential;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialRequestOptions;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.authentication.HttpSessionPublicKeyCredentialRequestOptionsRepository;
import org.springframework.security.web.webauthn.authentication.PublicKeyCredentialRequestOptionsRepository;
import org.springframework.security.web.webauthn.jackson.WebauthnJacksonModule;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.RelyingPartyAuthenticationRequest;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.security.web.webauthn.management.WebAuthnRelyingPartyOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.json.JsonMapper;

@Component
public class WebAuthnMfaProvider implements MfaProvider {

	private final PublicKeyCredentialUserEntityRepository userEntities;

	private final UserCredentialRepository userCredentials;

	private final WebAuthnRelyingPartyOperations relyingParty;

	private final PublicKeyCredentialRequestOptionsRepository requestOptions = new HttpSessionPublicKeyCredentialRequestOptionsRepository();

	private final JsonMapper json = JsonMapper.builder().addModule(new WebauthnJacksonModule()).build();

	public WebAuthnMfaProvider(PublicKeyCredentialUserEntityRepository userEntities,
			UserCredentialRepository userCredentials, WebAuthnRelyingPartyOperations relyingParty) {
		this.userEntities = userEntities;
		this.userCredentials = userCredentials;
		this.relyingParty = relyingParty;
	}

	@Override
	public String authority() {
		return FactorGrantedAuthority.WEBAUTHN_AUTHORITY;
	}

	@Override
	public String method() {
		return "webauthn";
	}

	@Override
	public String label() {
		return "Passkey";
	}

	@Override
	public boolean enabled(User user) {
		PublicKeyCredentialUserEntity entity = this.userEntities.findByUsername(user.username());
		return entity != null && !this.userCredentials.findByUserId(entity.getId()).isEmpty();
	}

	@Override
	public boolean verify(User user, String code) {
		if (!StringUtils.hasText(code) || !(RequestContextHolder
			.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
			return false;
		}
		HttpServletRequest request = attrs.getRequest();
		PublicKeyCredentialRequestOptions options = this.requestOptions.load(request);
		if (options == null) {
			return false;
		}
		try {
			PublicKeyCredential<AuthenticatorAssertionResponse> credential = this.json.readValue(code,
					this.json.getTypeFactory()
						.constructParametricType(PublicKeyCredential.class, AuthenticatorAssertionResponse.class));
			PublicKeyCredentialUserEntity entity = this.relyingParty
				.authenticate(new RelyingPartyAuthenticationRequest(options, credential));
			clearOptions(request, attrs.getResponse());
			return user.username().equals(entity.getName());
		}
		catch (RuntimeException ex) {
			return false;
		}
	}

	private void clearOptions(HttpServletRequest request, HttpServletResponse response) {
		if (response != null) {
			this.requestOptions.save(request, response, null);
		}
	}

}
