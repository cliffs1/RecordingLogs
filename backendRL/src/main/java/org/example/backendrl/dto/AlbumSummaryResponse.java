package org.example.backendrl.dto;

import org.example.backendrl.entity.Album;

import java.time.LocalDate;

public class AlbumSummaryResponse {

    private Long id;
    private String title;
    private LocalDate releaseDate;
    private String coverUrl;

    public AlbumSummaryResponse() {
    }

    public AlbumSummaryResponse(
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

    public static AlbumSummaryResponse fromEntity(Album album) {
        return new AlbumSummaryResponse(
                album.getId(),
                album.getTitle(),
                album.getReleaseDate(),
                album.getCoverUrl()
        );
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
