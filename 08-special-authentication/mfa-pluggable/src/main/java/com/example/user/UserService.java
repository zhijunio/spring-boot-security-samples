package com.example.user;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

	private final JdbcClient jdbcClient;

	public UserService(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public User findByUsername(String username) {
		return this.jdbcClient.sql("""
				SELECT username, password, email, totp_secret, totp_mfa_enabled, email_mfa_enabled,
				       mnemonic_phrase_hash, mnemonic_mfa_enabled
				FROM users WHERE username = ?
				""")
			.param(username)
			.query(User.class)
			.single();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			return findByUsername(username);
		}
		catch (EmptyResultDataAccessException e) {
			throw new UsernameNotFoundException("user not found", e);
		}
	}

	@Transactional
	public void insert(User user) {
		this.jdbcClient.sql("""
				INSERT INTO users(username, password, email, totp_secret, totp_mfa_enabled, email_mfa_enabled,
				                  mnemonic_phrase_hash, mnemonic_mfa_enabled)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""")
			.params(user.username(), user.password(), user.email(), user.totpSecret(), user.totpMfaEnabled(),
					user.emailMfaEnabled(), user.mnemonicPhraseHash(), user.mnemonicMfaEnabled())
			.update();
	}

	@Transactional
	public User enableTotp(User user) {
		this.jdbcClient.sql("UPDATE users SET totp_mfa_enabled = true WHERE username = ?")
			.param(user.username())
			.update();
		return user.enableTotp();
	}

	@Transactional
	public User enableEmailMfa(User user) {
		this.jdbcClient.sql("UPDATE users SET email_mfa_enabled = true WHERE username = ?")
			.param(user.username())
			.update();
		return user.enableEmailMfa();
	}

	@Transactional
	public User enableMnemonic(User user, String mnemonicPhraseHash) {
		this.jdbcClient.sql("UPDATE users SET mnemonic_phrase_hash = ?, mnemonic_mfa_enabled = true WHERE username = ?")
			.params(mnemonicPhraseHash, user.username())
			.update();
		return user.enableMnemonic(mnemonicPhraseHash);
	}

}
