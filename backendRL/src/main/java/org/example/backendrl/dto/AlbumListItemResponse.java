package org.example.backendrl.dto;

import org.example.backendrl.entity.AlbumListItem;

public class AlbumListItemResponse {

    private Long albumId;
    private String title;
    private String coverUrl;
    private Integer position;
    private Long artistId;
    private String artistName;

    public AlbumListItemResponse() {
    }

    public AlbumListItemResponse(
            Long albumId,
            String title,
            String coverUrl,
            Integer position,
            Long artistId,
            String artistName) {

        this.albumId = albumId;
        this.title = title;
        this.coverUrl = coverUrl;
        this.position = position;
        this.artistId = artistId;
        this.artistName = artistName;
    }

    public static AlbumListItemResponse fromEntity(AlbumListItem item) {
        return new AlbumListItemResponse(
                item.getAlbum().getId(),
                item.getAlbum().getTitle(),
                item.getAlbum().getCoverUrl(),
                item.getPosition(),
                item.getAlbum().getArtist().getId(),
                item.getAlbum().getArtist().getName()
        );
    }

    public Long getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
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
