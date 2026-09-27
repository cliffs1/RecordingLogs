package org.example.backendrl.dto;

import org.example.backendrl.entity.Song;

import java.time.LocalDate;

public class SongSummaryResponse {

    private Long id;
    private String title;
    private Integer durationSeconds;
    private LocalDate originalReleaseDate;

    public SongSummaryResponse() {
    }

    public SongSummaryResponse(
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

    public static SongSummaryResponse fromEntity(Song song) {
        return new SongSummaryResponse(
                song.getId(),
                song.getTitle(),
                song.getDurationSeconds(),
                song.getOriginalReleaseDate()
        );
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
