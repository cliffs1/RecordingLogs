package org.example.backendrl.dto;

import jakarta.validation.constraints.NotNull;
import org.example.backendrl.entity.Role;

public class RoleUpdateRequest {

    @NotNull(message = "Role is required")
    private Role role;

    public RoleUpdateRequest() {
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
