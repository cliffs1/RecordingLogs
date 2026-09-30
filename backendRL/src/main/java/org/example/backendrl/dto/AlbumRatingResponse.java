package org.example.backendrl.dto;

import org.example.backendrl.entity.AlbumRating;

import java.time.LocalDateTime;

public class AlbumRatingResponse {

    private Long id;
    private Long userId;
    private String username;
    private Long albumId;
    private Integer rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AlbumRatingResponse() {
    }

    public AlbumRatingResponse(
            Long id,
            Long userId,
            String username,
            Long albumId,
            Integer rating,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.userId = userId;
        this.username = username;
        this.albumId = albumId;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AlbumRatingResponse fromEntity(AlbumRating albumRating) {

        return new AlbumRatingResponse(
                albumRating.getId(),
                albumRating.getUser().getId(),
                albumRating.getUser().getUsername(),
                albumRating.getAlbum().getId(),
                albumRating.getRating(),
                albumRating.getCreatedAt(),
                albumRating.getUpdatedAt()
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

    public Long getAlbumId() {
        return albumId;
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
