package org.example.backendrl.dto;

import java.time.LocalDate;

public class AlbumResponse extends HypermediaResponse {

    private Long id;
    private Long artistId;
    private String title;
    private LocalDate releaseDate;
    private String coverUrl;

    public AlbumResponse() {
    }

    public AlbumResponse(
            Long id,
            Long artistId,
            String title,
            LocalDate releaseDate,
            String coverUrl
    ) {
        this.id = id;
        this.artistId = artistId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.coverUrl = coverUrl;
    }

    public Long getId() {
        return id;
    }

    public Long getArtistId() {
        return artistId;
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
