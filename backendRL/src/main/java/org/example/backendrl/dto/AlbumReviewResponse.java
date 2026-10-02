package org.example.backendrl.dto;

import org.example.backendrl.entity.AlbumReview;

import java.time.LocalDateTime;

public class AlbumReviewResponse {

    private Long id;
    private Long userId;
    private String username;
    private Long albumId;
    private String albumTitle;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AlbumReviewResponse() {
    }

    public AlbumReviewResponse(
            Long id,
            Long userId,
            String username,
            Long albumId,
            String albumTitle,
            String title,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.userId = userId;
        this.username = username;
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AlbumReviewResponse fromEntity(AlbumReview review) {
        return new AlbumReviewResponse(
                review.getId(),
                review.getUser().getId(),
                review.getUser().getUsername(),
                review.getAlbum().getId(),
                review.getAlbum().getTitle(),
                review.getTitle(),
                review.getContent(),
                review.getCreatedAt(),
                review.getUpdatedAt()
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

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
