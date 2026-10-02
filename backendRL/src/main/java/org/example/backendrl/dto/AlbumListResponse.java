package org.example.backendrl.dto;

import org.example.backendrl.entity.AlbumList;
import org.example.backendrl.entity.AlbumListItem;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class AlbumListResponse {

    private Long id;
    private Long userId;
    private String username;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AlbumListItemResponse> albums;

    public AlbumListResponse() {
    }

    public AlbumListResponse(
            Long id,
            Long userId,
            String username,
            String name,
            String description,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<AlbumListItemResponse> albums) {

        this.id = id;
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.albums = albums;
    }

    public static AlbumListResponse fromEntity(AlbumList albumList) {
        List<AlbumListItemResponse> albums = albumList.getAlbums()
                .stream()
                .sorted(Comparator.comparing(AlbumListItem::getPosition))
                .map(AlbumListItemResponse::fromEntity)
                .toList();

        return new AlbumListResponse(
                albumList.getId(),
                albumList.getUser().getId(),
                albumList.getUser().getUsername(),
                albumList.getName(),
                albumList.getDescription(),
                albumList.getCreatedAt(),
                albumList.getUpdatedAt(),
                albums
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<AlbumListItemResponse> getAlbums() {
        return albums;
    }
}
