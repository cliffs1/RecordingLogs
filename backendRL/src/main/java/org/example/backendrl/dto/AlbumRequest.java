package org.example.backendrl.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class AlbumRequest {

    @NotBlank(message = "Album title is required")
    private String title;

    private LocalDate releaseDate;

    private String coverUrl;

    public AlbumRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
