package org.example.backendrl.controller;

import org.example.backendrl.dto.AlbumSongRequest;
import org.example.backendrl.dto.AlbumSongResponse;
import org.example.backendrl.service.AlbumSongService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumSongController {

    private final AlbumSongService albumSongService;

    public AlbumSongController(
            AlbumSongService albumSongService
    ) {
        this.albumSongService = albumSongService;
    }

    @GetMapping("/{albumId}/songs")
    public List<AlbumSongResponse> getSongsForAlbum(
            @PathVariable Long albumId
    ) {
        return albumSongService.getSongsForAlbum(albumId);
    }

    @PostMapping("/{albumId}/songs/{songId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addSongToAlbum(
            @PathVariable Long albumId,
            @PathVariable Long songId,
            @Valid @RequestBody AlbumSongRequest request
    ) {
        albumSongService.addSongToAlbum(
                albumId,
                songId,
                request.getTrackNumber(),
                request.getDiscNumber()
        );
    }

    @DeleteMapping("/{albumId}/songs/{songId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSongFromAlbum(
            @PathVariable Long albumId,
            @PathVariable Long songId
    ) {
        albumSongService.removeSongFromAlbum(albumId, songId);
    }
}
