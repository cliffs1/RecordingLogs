package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.AlbumSummaryResponse;
import org.example.backendrl.service.AlbumSongService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@Tag(name = "Songs")
public class SongAlbumController {

    private final AlbumSongService albumSongService;

    public SongAlbumController(
            AlbumSongService albumSongService
    ) {
        this.albumSongService = albumSongService;
    }

    @GetMapping("/{songId}/albums")
    @Operation(summary = "Get all albums containing a song")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of albums"),
            @ApiResponse(responseCode = "404", description = "Song not found")
    })
    public List<AlbumSummaryResponse> getAlbumsForSong(
            @PathVariable Long songId
    ) {
        return albumSongService.getAlbumsForSong(songId);
    }
}
