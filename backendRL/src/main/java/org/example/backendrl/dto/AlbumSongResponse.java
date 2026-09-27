package org.example.backendrl.dto;

import org.example.backendrl.entity.AlbumSong;

public class AlbumSongResponse {

    private Long id;
    private Long albumId;
    private Long songId;
    private String songTitle;
    private Integer trackNumber;
    private Integer discNumber;

    public AlbumSongResponse() {
    }

    public AlbumSongResponse(
            Long id,
            Long albumId,
            Long songId,
            String songTitle,
            Integer trackNumber,
            Integer discNumber
    ) {
        this.id = id;
        this.albumId = albumId;
        this.songId = songId;
        this.songTitle = songTitle;
        this.trackNumber = trackNumber;
        this.discNumber = discNumber;
    }

    public static AlbumSongResponse fromEntity(AlbumSong albumSong) {
        return new AlbumSongResponse(
                albumSong.getId(),
                albumSong.getAlbum().getId(),
                albumSong.getSong().getId(),
                albumSong.getSong().getTitle(),
                albumSong.getTrackNumber(),
                albumSong.getDiscNumber()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public Long getSongId() {
        return songId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public Integer getDiscNumber() {
        return discNumber;
    }
}
