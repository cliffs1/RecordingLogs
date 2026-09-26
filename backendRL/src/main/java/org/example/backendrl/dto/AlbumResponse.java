package org.example.backendrl.dto;

import java.time.LocalDate;

public class AlbumResponse {

    private Long id;
    private String title;
    private LocalDate releaseDate;
    private String coverUrl;

    public AlbumResponse() {
    }

    public AlbumResponse(
            Long id,
            String title,
            LocalDate releaseDate,
            String coverUrl
    ) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.coverUrl = coverUrl;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public String getCoverUrl() {
        return coverUrl;
    }
}
