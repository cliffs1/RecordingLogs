package org.example.backendrl.dto;

import org.example.backendrl.entity.Song;

import java.time.LocalDate;

public class SongSummaryResponse {

    private Long id;
    private String title;
    private Integer durationSeconds;
    private LocalDate originalReleaseDate;
    private Long albumId;
    private String albumTitle;
    private Long artistId;
    private String artistName;

    public SongSummaryResponse() {
    }

    public SongSummaryResponse(
            Long id,
            String title,
            Integer durationSeconds,
            LocalDate originalReleaseDate,
            Long albumId,
            String albumTitle,
            Long artistId,
            String artistName
    ) {
        this.id = id;
        this.title = title;
        this.durationSeconds = durationSeconds;
        this.originalReleaseDate = originalReleaseDate;
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.artistId = artistId;
        this.artistName = artistName;
    }

    public static SongSummaryResponse fromEntity(Song song) {
        return new SongSummaryResponse(
                song.getId(),
                song.getTitle(),
                song.getDurationSeconds(),
                song.getOriginalReleaseDate(),
                song.getAlbum().getId(),
                song.getAlbum().getTitle(),
                song.getAlbum().getArtist().getId(),
                song.getAlbum().getArtist().getName()
        );
    }

    public Long getAlbumId() {
        return albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public Long getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
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
