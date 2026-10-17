package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.backendrl.dto.LoginRequest;
import org.example.backendrl.dto.TokenResponse;
import org.example.backendrl.dto.UserRequest;
import org.example.backendrl.dto.UserResponse;
import org.example.backendrl.exception.UnauthorizedException;
import org.example.backendrl.security.JwtProperties;
import org.example.backendrl.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register, login, token refresh and logout")
public class AuthController {

    public static final String REFRESH_COOKIE = "refresh_token";
    private static final String REFRESH_COOKIE_PATH = "/api/auth";

    private final AuthService authService;
    private final Duration refreshTtl;
    private final boolean secureCookie;

    public AuthController(
            AuthService authService,
            JwtProperties jwtProperties,
            @Value("${app.cookie.secure}") boolean secureCookie) {

        this.authService = authService;
        this.refreshTtl = jwtProperties.refreshTtl();
        this.secureCookie = secureCookie;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirements
    @Operation(summary = "Register a new account (role USER)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "409", description = "Username or email already taken")
    })
    public UserResponse register(@Valid @RequestBody UserRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Log in with username or email",
            description = "Returns a short-lived access token and sets the refresh token as an HttpOnly cookie.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logged in"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Wrong credentials")
    })
    public TokenResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        return withRefreshCookie(authService.login(request), response);
    }

    @PostMapping("/refresh")
    @SecurityRequirements
    @Operation(summary = "Get a new access token using the refresh token cookie",
            description = "The refresh token is rotated: the old one stops working and a new cookie is set.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New tokens issued"),
            @ApiResponse(responseCode = "401", description = "Refresh token missing, expired, revoked or reused")
    })
    public TokenResponse refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response) {

        try {
            return withRefreshCookie(authService.refresh(refreshToken), response);
        } catch (UnauthorizedException exception) {
            clearRefreshCookie(response);
            throw exception;
        }
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Log out",
            description = "Revokes the refresh token and invalidates the current access token.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logged out"),
            @ApiResponse(responseCode = "401", description = "Not logged in")
    })
    public void logout(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response) {

        authService.logout(refreshToken);
        clearRefreshCookie(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get the logged in user's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current user"),
            @ApiResponse(responseCode = "401", description = "Not logged in")
    })
    public UserResponse me() {
        return authService.me();
    }

    private TokenResponse withRefreshCookie(
            AuthService.Tokens tokens,
            HttpServletResponse response) {

        response.addHeader(HttpHeaders.SET_COOKIE,
                refreshCookie(tokens.refreshToken(), refreshTtl).toString());

        return tokens.response();
    }

    private void clearRefreshCookie(HttpServletResponse response) {

        response.addHeader(HttpHeaders.SET_COOKIE,
                refreshCookie("", Duration.ZERO).toString());
    }

    private ResponseCookie refreshCookie(String value, Duration maxAge) {

        return ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path(REFRESH_COOKIE_PATH)
                .maxAge(maxAge)
                .build();
    }
}
