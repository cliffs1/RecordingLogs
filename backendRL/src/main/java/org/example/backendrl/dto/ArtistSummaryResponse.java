package org.example.backendrl.dto;

import org.example.backendrl.entity.Artist;

public class ArtistSummaryResponse {

    private Long id;
    private String name;

    public ArtistSummaryResponse() {
    }

    public ArtistSummaryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ArtistSummaryResponse fromEntity(Artist artist) {
        return new ArtistSummaryResponse(
                artist.getId(),
                artist.getName()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
