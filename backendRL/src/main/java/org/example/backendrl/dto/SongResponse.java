package org.example.backendrl.dto;

import java.time.LocalDate;

public class SongResponse extends HypermediaResponse {

    private Long id;
    private Long albumId;
    private String title;
    private Integer durationSeconds;
    private LocalDate originalReleaseDate;
    private Integer trackNumber;
    private Integer discNumber;

    public SongResponse() {
    }

    public SongResponse(
            Long id,
            Long albumId,
            String title,
            Integer durationSeconds,
            LocalDate originalReleaseDate,
            Integer trackNumber,
            Integer discNumber
    ) {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.durationSeconds = durationSeconds;
        this.originalReleaseDate = originalReleaseDate;
        this.trackNumber = trackNumber;
        this.discNumber = discNumber;
    }

    public Long getId() {
        return id;
    }

    public Long getAlbumId() {
        return albumId;
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

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public Integer getDiscNumber() {
        return discNumber;
    }
}
