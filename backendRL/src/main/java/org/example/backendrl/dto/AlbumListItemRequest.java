package org.example.backendrl.dto;

import jakarta.validation.constraints.Min;

public class AlbumListItemRequest {

    @Min(value = 1, message = "Position must be at least 1")
    private Integer position;

    public AlbumListItemRequest() {
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
