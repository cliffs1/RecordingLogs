package org.example.backendrl.dto;

import org.example.backendrl.entity.AlbumFavorite;

import java.time.LocalDateTime;

public class AlbumFavoriteResponse {

    private Long id;
    private Long userId;
    private Long albumId;
    private String albumTitle;
    private String coverUrl;
    private LocalDateTime createdAt;

    public AlbumFavoriteResponse() {
    }

    public AlbumFavoriteResponse(
            Long id,
            Long userId,
            Long albumId,
            String albumTitle,
            String coverUrl,
            LocalDateTime createdAt) {

        this.id = id;
        this.userId = userId;
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.coverUrl = coverUrl;
        this.createdAt = createdAt;
    }

    public static AlbumFavoriteResponse fromEntity(AlbumFavorite favorite) {
        return new AlbumFavoriteResponse(
                favorite.getId(),
                favorite.getUser().getId(),
                favorite.getAlbum().getId(),
                favorite.getAlbum().getTitle(),
                favorite.getAlbum().getCoverUrl(),
                favorite.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
