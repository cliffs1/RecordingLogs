package org.example.backendrl.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AlbumListItemRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @Min(value = 1, message = "Position must be at least 1")
    private Integer position;

    public AlbumListItemRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
