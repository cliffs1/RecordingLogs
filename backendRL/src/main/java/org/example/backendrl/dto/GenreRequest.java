package org.example.backendrl.dto;

import jakarta.validation.constraints.NotBlank;

public class GenreRequest {

    @NotBlank(message = "Genre name is required")
    private String name;

    public GenreRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
