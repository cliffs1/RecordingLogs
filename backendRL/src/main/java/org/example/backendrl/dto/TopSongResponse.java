package org.example.backendrl.dto;

/**
 * One entry of the top-rated songs chart.
 * Built from Song, Album, Artist and the song's ratings.
 */
public class TopSongResponse extends HypermediaResponse {

    private int rank;
    private Long songId;
    private String title;
    private Integer durationSeconds;
    private Long albumId;
    private String albumTitle;
    private String coverUrl;
    private Long artistId;
    private String artistName;
    private double averageRating;
    private long ratingCount;

    public TopSongResponse() {
    }

    public TopSongResponse(
            int rank,
            Long songId,
            String title,
            Integer durationSeconds,
            Long albumId,
            String albumTitle,
            String coverUrl,
            Long artistId,
            String artistName,
            double averageRating,
            long ratingCount
    ) {
        this.rank = rank;
        this.songId = songId;
        this.title = title;
        this.durationSeconds = durationSeconds;
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.coverUrl = coverUrl;
        this.artistId = artistId;
        this.artistName = artistName;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
    }

    public int getRank() {
        return rank;
    }

    public Long getSongId() {
        return songId;
    }

    public String getTitle() {
        return title;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
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
