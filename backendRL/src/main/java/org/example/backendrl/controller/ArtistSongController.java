package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.SongSummaryResponse;
import org.example.backendrl.service.ArtistSongService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@Tag(name = "Artists")
public class ArtistSongController {

    private final ArtistSongService artistSongService;

    public ArtistSongController(
            ArtistSongService artistSongService
    ) {
        this.artistSongService = artistSongService;
    }

    @GetMapping("/{artistId}/songs")
    @Operation(summary = "Get all songs of an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of songs"),
            @ApiResponse(responseCode = "404", description = "Artist not found")
    })
    public List<SongSummaryResponse> getSongsForArtist(
            @PathVariable Long artistId
    ) {
        return artistSongService.getSongsForArtist(artistId);
    }

    @PostMapping("/{artistId}/songs/{songId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a song to an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Song added to artist"),
            @ApiResponse(responseCode = "404", description = "Artist or song not found"),
            @ApiResponse(responseCode = "409", description = "Song is already linked to this artist")
    })
    public void addSongToArtist(
            @PathVariable Long artistId,
            @PathVariable Long songId
    ) {
        artistSongService.addSongToArtist(
                artistId,
                songId
        );
    }

    @DeleteMapping("/{artistId}/songs/{songId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a song from an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Song removed from artist"),
            @ApiResponse(responseCode = "404", description = "Artist, song or link not found")
    })
    public void removeSongFromArtist(
            @PathVariable Long artistId,
            @PathVariable Long songId
    ) {
        artistSongService.removeSongFromArtist(
                artistId,
                songId
        );
    }
}