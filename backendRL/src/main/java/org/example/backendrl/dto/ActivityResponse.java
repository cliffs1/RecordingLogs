package org.example.backendrl.dto;

import java.time.LocalDateTime;

public class ActivityResponse {

    private String type;
    private Long userId;
    private String username;
    private Long targetId;
    private String targetTitle;
    private String value;
    private LocalDateTime timestamp;
    /** Album the activity belongs to (the song's album for SONG_RATING). */
    private Long albumId;
    private Long artistId;

    public ActivityResponse() {
    }

    public ActivityResponse(
            String type,
            Long userId,
            String username,
            Long targetId,
            String targetTitle,
            String value,
            LocalDateTime timestamp,
            Long albumId,
            Long artistId) {

        this.type = type;
        this.userId = userId;
        this.username = username;
        this.targetId = targetId;
        this.targetTitle = targetTitle;
        this.value = value;
        this.timestamp = timestamp;
        this.albumId = albumId;
        this.artistId = artistId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public Long getArtistId() {
        return artistId;
    }

    public String getType() {
        return type;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Long getTargetId() {
        return targetId;
    }

    public String getTargetTitle() {
        return targetTitle;
    }

    public String getValue() {
        return value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
