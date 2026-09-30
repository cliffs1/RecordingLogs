package org.example.backendrl.dto;

import org.example.backendrl.entity.SongRating;

import java.time.LocalDateTime;

public class SongRatingResponse {

    private Long id;
    private Long userId;
    private String username;
    private Long songId;
    private Integer rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SongRatingResponse() {
    }

    public SongRatingResponse(
            Long id,
            Long userId,
            String username,
            Long songId,
            Integer rating,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.userId = userId;
        this.username = username;
        this.songId = songId;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SongRatingResponse fromEntity(SongRating songRating) {

        return new SongRatingResponse(
                songRating.getId(),
                songRating.getUser().getId(),
                songRating.getUser().getUsername(),
                songRating.getSong().getId(),
                songRating.getRating(),
                songRating.getCreatedAt(),
                songRating.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Long getSongId() {
        return songId;
    }

    public Integer getRating() {
        return rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
