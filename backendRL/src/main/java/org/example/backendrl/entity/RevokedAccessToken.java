package org.example.backendrl.entity;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * Access token invalidated by logout. Kept until the token would have expired anyway.
 */
@Entity
@Table(name = "revoked_access_tokens")
public class RevokedAccessToken {

    @Id
    @Column(length = 36)
    private String jti;

    @Column(nullable = false)
    private Instant expiresAt;

    public RevokedAccessToken() {
    }

    public RevokedAccessToken(String jti, Instant expiresAt) {
        this.jti = jti;
        this.expiresAt = expiresAt;
    }

    public String getJti() {
        return jti;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
