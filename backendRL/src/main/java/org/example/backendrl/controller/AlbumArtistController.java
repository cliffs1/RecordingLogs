package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.ArtistSummaryResponse;
import org.example.backendrl.service.AlbumArtistService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@Tag(name = "Albums")
public class AlbumArtistController {

    private final AlbumArtistService albumArtistService;

    public AlbumArtistController(
            AlbumArtistService albumArtistService
    ) {
        this.albumArtistService = albumArtistService;
    }

    @GetMapping("/{albumId}/artists")
    @Operation(summary = "Get all artists of an album")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of artists"),
            @ApiResponse(responseCode = "404", description = "Album not found")
    })
    public List<ArtistSummaryResponse> getArtistsForAlbum(
            @PathVariable Long albumId
    ) {
        return albumArtistService.getArtistsForAlbum(albumId);
    }

    @PostMapping("/{albumId}/artists/{artistId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add an artist to an album")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Artist added to album"),
            @ApiResponse(responseCode = "404", description = "Album or artist not found"),
            @ApiResponse(responseCode = "409", description = "Artist is already on this album")
    })
    public void addArtistToAlbum(
            @PathVariable Long albumId,
            @PathVariable Long artistId
    ) {
        albumArtistService.addArtistToAlbum(albumId, artistId);
    }

    @DeleteMapping("/{albumId}/artists/{artistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove an artist from an album")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Artist removed from album"),
            @ApiResponse(responseCode = "404", description = "Album, artist or link not found")
    })
    public void removeArtistFromAlbum(
            @PathVariable Long albumId,
            @PathVariable Long artistId
    ) {
        albumArtistService.removeArtistFromAlbum(albumId, artistId);
    }
}
