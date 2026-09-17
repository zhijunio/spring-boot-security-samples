package com.example.config;

import java.util.List;

import org.springframework.security.web.webauthn.api.Bytes;
import org.springframework.security.web.webauthn.api.CredentialRecord;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;

final class OnePasskeyUserCredentialRepository implements UserCredentialRepository {

    private final UserCredentialRepository delegate;

    OnePasskeyUserCredentialRepository(UserCredentialRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public void delete(Bytes credentialId) {
        this.delegate.delete(credentialId);
    }

    @Override
    public void save(CredentialRecord record) {
        if (this.delegate.findByCredentialId(record.getCredentialId()) == null
                && !this.delegate.findByUserId(record.getUserEntityUserId()).isEmpty()) {
            throw new IllegalStateException("This account already has a passkey");
        }
        this.delegate.save(record);
    }

    @Override
    public CredentialRecord findByCredentialId(Bytes credentialId) {
        return this.delegate.findByCredentialId(credentialId);
    }

    @Override
    public List<CredentialRecord> findByUserId(Bytes userId) {
        return this.delegate.findByUserId(userId);
    }

}
