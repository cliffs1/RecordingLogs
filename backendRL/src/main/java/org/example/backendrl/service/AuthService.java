package org.example.backendrl.service;

import org.example.backendrl.dto.LoginRequest;
import org.example.backendrl.dto.TokenResponse;
import org.example.backendrl.dto.UserRequest;
import org.example.backendrl.dto.UserResponse;
import org.example.backendrl.entity.RevokedAccessToken;
import org.example.backendrl.entity.User;
import org.example.backendrl.exception.UnauthorizedException;
import org.example.backendrl.repository.RevokedAccessTokenRepository;
import org.example.backendrl.repository.UserRepository;
import org.example.backendrl.security.CurrentUser;
import org.example.backendrl.security.JwtService;
import org.example.backendrl.security.RefreshTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    /**
     * Access token + raw refresh token. The refresh token is sent to the client as a cookie.
     */
    public record Tokens(TokenResponse response, String refreshToken) {
    }

    private final UserService userService;
    private final UserRepository userRepository;
    private final RevokedAccessTokenRepository revokedAccessTokenRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;

    public AuthService(
            UserService userService,
            UserRepository userRepository,
            RevokedAccessTokenRepository revokedAccessTokenRepository,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            PasswordEncoder passwordEncoder,
            CurrentUser currentUser) {

        this.userService = userService;
        this.userRepository = userRepository;
        this.revokedAccessTokenRepository = revokedAccessTokenRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.currentUser = currentUser;
    }

    public UserResponse register(UserRequest request) {
        return userService.createUser(request);
    }

    @Transactional
    public Tokens login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getLogin())
                .or(() -> userRepository.findByEmail(request.getLogin()))
                .filter(found -> passwordEncoder.matches(request.getPassword(), found.getPassword()))
                .orElseThrow(() -> new UnauthorizedException("Invalid username/email or password"));

        return issueTokens(user);
    }

    @Transactional(noRollbackFor = UnauthorizedException.class)
    public Tokens refresh(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Refresh token is missing");
        }

        User user = refreshTokenService.rotate(refreshToken);

        return issueTokens(user);
    }

    /**
     * Revokes the refresh token and blacklists the current access token until it expires.
     */
    @Transactional
    public void logout(String refreshToken) {

        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.revoke(refreshToken);
        }

        Jwt jwt = currentUser.jwt();
        revokedAccessTokenRepository.save(new RevokedAccessToken(jwt.getId(), jwt.getExpiresAt()));
    }

    public UserResponse me() {
        return userService.getUserById(currentUser.id());
    }

    private Tokens issueTokens(User user) {

        TokenResponse response = new TokenResponse(
                jwtService.createAccessToken(user),
                jwtService.accessTtlSeconds(),
                UserResponse.fromEntity(user)
        );

        return new Tokens(response, refreshTokenService.issue(user));
    }
}
