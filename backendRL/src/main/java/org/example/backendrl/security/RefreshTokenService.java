package org.example.backendrl.security;

import org.example.backendrl.entity.RefreshToken;
import org.example.backendrl.entity.User;
import org.example.backendrl.exception.UnauthorizedException;
import org.example.backendrl.repository.RefreshTokenRepository;
import org.example.backendrl.repository.RevokedAccessTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Issues, rotates and revokes refresh tokens. Each refresh replaces the old token with a new one;
 * presenting an already used (revoked) token revokes every token of that user.
 */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RevokedAccessTokenRepository revokedAccessTokenRepository;
    private final JwtProperties properties;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            RevokedAccessTokenRepository revokedAccessTokenRepository,
            JwtProperties properties) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.revokedAccessTokenRepository = revokedAccessTokenRepository;
        this.properties = properties;
    }

    /**
     * Creates a new refresh token for the user and returns its raw value.
     */
    @Transactional
    public String issue(User user) {

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(hash(raw));
        token.setExpiresAt(Instant.now().plus(properties.refreshTtl()));

        refreshTokenRepository.save(token);

        return raw;
    }

    /**
     * Revokes the given token and returns the user it belonged to, so a new pair can be issued.
     * noRollbackFor keeps the "revoke everything" step when token reuse is detected.
     */
    @Transactional(noRollbackFor = UnauthorizedException.class)
    public User rotate(String raw) {

        RefreshToken token = refreshTokenRepository.findByTokenHash(hash(raw))
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (token.isRevoked()) {
            refreshTokenRepository.revokeAllByUserId(token.getUser().getId());
            throw new UnauthorizedException("Refresh token has already been used");
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token has expired");
        }

        token.setRevoked(true);

        return token.getUser();
    }

    @Transactional
    public void revoke(String raw) {

        refreshTokenRepository.findByTokenHash(hash(raw))
                .ifPresent(token -> token.setRevoked(true));
    }

    @Transactional
    public void revokeAll(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void cleanUp() {

        Instant now = Instant.now();

        refreshTokenRepository.deleteByExpiresAtBefore(now);
        revokedAccessTokenRepository.deleteByExpiresAtBefore(now);
    }

    private static String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
