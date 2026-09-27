package org.example.backendrl.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class SongRequest {

    @NotBlank(message = "Song title is required")
    private String title;

    private Integer durationSeconds;

    private LocalDate originalReleaseDate;

    public SongRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public LocalDate getOriginalReleaseDate() {
        return originalReleaseDate;
    }

    public void setOriginalReleaseDate(LocalDate originalReleaseDate) {
        this.originalReleaseDate = originalReleaseDate;
    }
}
