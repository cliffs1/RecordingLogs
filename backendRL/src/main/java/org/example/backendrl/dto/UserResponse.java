package org.example.backendrl.dto;

import org.example.backendrl.entity.Role;
import org.example.backendrl.entity.User;

public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String displayName;
    private Role role;

    public UserResponse() {
    }

    public UserResponse(
            Long id,
            String username,
            String email,
            String displayName,
            Role role) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
    }

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole()
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

    public Role getRole() {
        return role;
    }
}
