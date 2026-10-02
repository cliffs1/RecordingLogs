package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.AlbumSongRequest;
import org.example.backendrl.dto.AlbumSongResponse;
import org.example.backendrl.service.AlbumSongService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@Tag(name = "Albums")
public class AlbumSongController {

    private final AlbumSongService albumSongService;

    public AlbumSongController(
            AlbumSongService albumSongService
    ) {
        this.albumSongService = albumSongService;
    }

    @GetMapping("/{albumId}/songs")
    @Operation(summary = "Get the tracklist of an album")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of songs"),
            @ApiResponse(responseCode = "404", description = "Album not found")
    })
    public List<AlbumSongResponse> getSongsForAlbum(
            @PathVariable Long albumId
    ) {
        return albumSongService.getSongsForAlbum(albumId);
    }

    @PostMapping("/{albumId}/songs/{songId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a song to an album's tracklist")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Song added to album"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Album or song not found"),
            @ApiResponse(responseCode = "409", description = "Song is already on this album")
    })
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
    @Operation(summary = "Remove a song from an album's tracklist")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Song removed from album"),
            @ApiResponse(responseCode = "404", description = "Album, song or link not found")
    })
    public void removeSongFromAlbum(
            @PathVariable Long albumId,
            @PathVariable Long songId
    ) {
        albumSongService.removeSongFromAlbum(albumId, songId);
    }
}
