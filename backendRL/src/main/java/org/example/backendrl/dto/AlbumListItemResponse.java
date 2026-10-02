package org.example.backendrl.dto;

import org.example.backendrl.entity.AlbumListItem;

public class AlbumListItemResponse {

    private Long albumId;
    private String title;
    private String coverUrl;
    private Integer position;

    public AlbumListItemResponse() {
    }

    public AlbumListItemResponse(
            Long albumId,
            String title,
            String coverUrl,
            Integer position) {

        this.albumId = albumId;
        this.title = title;
        this.coverUrl = coverUrl;
        this.position = position;
    }

    public static AlbumListItemResponse fromEntity(AlbumListItem item) {
        return new AlbumListItemResponse(
                item.getAlbum().getId(),
                item.getAlbum().getTitle(),
                item.getAlbum().getCoverUrl(),
                item.getPosition()
        );
    }

    public Long getAlbumId() {
        return albumId;
    }

    public String getTitle() {
        return title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public Integer getPosition() {
        return position;
    }
}
