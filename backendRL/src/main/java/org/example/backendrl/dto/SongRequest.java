package org.example.backendrl.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class SongRequest {

    @NotBlank(message = "Song title is required")
    private String title;

    private Integer durationSeconds;

    private LocalDate originalReleaseDate;

    @NotNull(message = "Track number is required")
    @Positive(message = "Track number must be positive")
    private Integer trackNumber;

    @Positive(message = "Disc number must be positive")
    private Integer discNumber;

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

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    public Integer getDiscNumber() {
        return discNumber;
    }

    public void setDiscNumber(Integer discNumber) {
        this.discNumber = discNumber;
    }
}
