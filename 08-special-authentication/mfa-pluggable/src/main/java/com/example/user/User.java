package com.example.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

public record User(String username, String password, String email, String totpSecret, boolean totpMfaEnabled,
		boolean emailMfaEnabled, String mnemonicPhraseHash, boolean mnemonicMfaEnabled)
		implements UserDetails, Serializable {

	public static User registered(String username, String password, String email, String totpSecret) {
		return new User(username, password, email, totpSecret, false, false, "", false);
	}

	public User enableTotp() {
		return new User(this.username, this.password, this.email, this.totpSecret, true, this.emailMfaEnabled,
				this.mnemonicPhraseHash, this.mnemonicMfaEnabled);
	}

	public User enableEmailMfa() {
		return new User(this.username, this.password, this.email, this.totpSecret, this.totpMfaEnabled, true,
				this.mnemonicPhraseHash, this.mnemonicMfaEnabled);
	}

	public User enableMnemonic(String mnemonicPhraseHash) {
		return new User(this.username, this.password, this.email, this.totpSecret, this.totpMfaEnabled,
				this.emailMfaEnabled, mnemonicPhraseHash, true);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.createAuthorityList("ROLE_USER");
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public String toString() {
		return "User{username='" + this.username + "', totpMfaEnabled=" + this.totpMfaEnabled + ", emailMfaEnabled="
				+ this.emailMfaEnabled + ", mnemonicMfaEnabled=" + this.mnemonicMfaEnabled + '}';
	}

}
