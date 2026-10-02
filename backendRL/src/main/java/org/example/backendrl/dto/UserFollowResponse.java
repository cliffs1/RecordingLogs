package org.example.backendrl.dto;

import org.example.backendrl.entity.User;

import java.time.LocalDateTime;

public class UserFollowResponse {

    private Long userId;
    private String username;
    private String displayName;
    private LocalDateTime followedAt;

    public UserFollowResponse() {
    }

    public UserFollowResponse(
            Long userId,
            String username,
            String displayName,
            LocalDateTime followedAt) {

        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.followedAt = followedAt;
    }

    public static UserFollowResponse of(User user, LocalDateTime followedAt) {
        return new UserFollowResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                followedAt
        );
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LocalDateTime getFollowedAt() {
        return followedAt;
    }
}
