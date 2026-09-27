package org.example.backendrl.dto;

import org.example.backendrl.entity.Genre;

public class GenreResponse {

    private Long id;
    private String name;

    public GenreResponse() {
    }

    public GenreResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static GenreResponse fromEntity(Genre genre) {
        return new GenreResponse(
                genre.getId(),
                genre.getName()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
