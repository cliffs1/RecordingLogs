package org.example.backendrl.dto;

import org.example.backendrl.entity.User;

public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String displayName;

    public UserResponse() {
    }

    public UserResponse(
            Long id,
            String username,
            String email,
            String displayName) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
    }

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName()
        );
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }
}
