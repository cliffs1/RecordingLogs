package org.example.backendrl.dto;

import java.time.LocalDate;

/**
 * One entry of the top-rated albums chart.
 * Built from Album, Artist and the album's ratings.
 */
public class TopAlbumResponse extends HypermediaResponse {

    private int rank;
    private Long albumId;
    private String title;
    private LocalDate releaseDate;
    private String coverUrl;
    private Long artistId;
    private String artistName;
    private double averageRating;
    private long ratingCount;

    public TopAlbumResponse() {
    }

    public TopAlbumResponse(
            int rank,
            Long albumId,
            String title,
            LocalDate releaseDate,
            String coverUrl,
            Long artistId,
            String artistName,
            double averageRating,
            long ratingCount
    ) {
        this.rank = rank;
        this.albumId = albumId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.coverUrl = coverUrl;
        this.artistId = artistId;
        this.artistName = artistName;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
    }

    public int getRank() {
        return rank;
    }

    public Long getAlbumId() {
        return albumId;
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

    public Long getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public long getRatingCount() {
        return ratingCount;
    }
}
