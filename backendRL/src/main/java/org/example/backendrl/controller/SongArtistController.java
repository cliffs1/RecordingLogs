package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.ArtistSummaryResponse;
import org.example.backendrl.service.ArtistSongService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@Tag(name = "Songs")
public class SongArtistController {

    private final ArtistSongService artistSongService;

    public SongArtistController(
            ArtistSongService artistSongService
    ) {
        this.artistSongService = artistSongService;
    }

    @GetMapping("/{songId}/artists")
    @Operation(summary = "Get all artists of a song")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of artists"),
            @ApiResponse(responseCode = "404", description = "Song not found")
    })
    public List<ArtistSummaryResponse> getArtistsForSong(
            @PathVariable Long songId
    ) {
        return artistSongService.getArtistsForSong(songId);
    }
}
