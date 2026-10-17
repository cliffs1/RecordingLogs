package org.example.backendrl.security;

import org.example.backendrl.entity.Role;
import org.example.backendrl.exception.ForbiddenException;
import org.example.backendrl.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Access to the authenticated user (from the JWT) and ownership checks used by the services.
 */
@Component
public class CurrentUser {

    public Jwt jwt() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new UnauthorizedException("Authentication required");
        }

        return jwt;
    }

    public Long id() {
        return Long.valueOf(jwt().getSubject());
    }

    public Role role() {
        return Role.valueOf(jwt().getClaimAsString(JwtService.ROLE_CLAIM));
    }

    public boolean isAdmin() {
        return role() == Role.ADMIN;
    }

    public boolean isModeratorOrAdmin() {
        Role role = role();
        return role == Role.MODERATOR || role == Role.ADMIN;
    }

    /**
     * Only the user themselves.
     */
    public void requireSelf(Long userId) {

        if (!id().equals(userId)) {
            throw new ForbiddenException("You can only perform this action for your own account");
        }
    }

    /**
     * The user themselves or an administrator.
     */
    public void requireSelfOrAdmin(Long userId) {

        if (!id().equals(userId) && !isAdmin()) {
            throw new ForbiddenException("You can only perform this action for your own account");
        }
    }

    /**
     * The owner of the content, a moderator or an administrator.
     */
    public void requireSelfOrModerator(Long ownerId) {

        if (!id().equals(ownerId) && !isModeratorOrAdmin()) {
            throw new ForbiddenException("You can only modify your own content");
        }
    }
}
