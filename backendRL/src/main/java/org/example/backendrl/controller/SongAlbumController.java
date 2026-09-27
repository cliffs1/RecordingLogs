package org.example.backendrl.controller;

import org.example.backendrl.dto.AlbumSummaryResponse;
import org.example.backendrl.service.AlbumSongService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongAlbumController {

    private final AlbumSongService albumSongService;

    public SongAlbumController(
            AlbumSongService albumSongService
    ) {
        this.albumSongService = albumSongService;
    }

    @GetMapping("/{songId}/albums")
    public List<AlbumSummaryResponse> getAlbumsForSong(
            @PathVariable Long songId
    ) {
        return albumSongService.getAlbumsForSong(songId);
    }
}
