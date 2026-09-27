package org.example.backendrl.dto;

import jakarta.validation.constraints.NotBlank;

public class ArtistRequest {

    @NotBlank(message = "Artist name is required")
    private String name;

    private String biography;

    public ArtistRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}