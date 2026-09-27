package org.example.backendrl.dto;

import java.time.LocalDate;

public class SongResponse {

    private Long id;
    private String title;
    private Integer durationSeconds;
    private LocalDate originalReleaseDate;

    public SongResponse() {
    }

    public SongResponse(
            Long id,
            String title,
            Integer durationSeconds,
            LocalDate originalReleaseDate
    ) {
        this.id = id;
        this.title = title;
        this.durationSeconds = durationSeconds;
        this.originalReleaseDate = originalReleaseDate;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public LocalDate getOriginalReleaseDate() {
        return originalReleaseDate;
    }
}
