package org.example.backendrl.dto;

public class ArtistResponse {

    private Long id;
    private String name;
    private String biography;

    public ArtistResponse() {
    }

    public ArtistResponse(Long id, String name, String biography) {
        this.id = id;
        this.name = name;
        this.biography = biography;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBiography() {
        return biography;
    }
}
