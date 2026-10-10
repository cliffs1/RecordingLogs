package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.dto.SongRequest;
import org.example.backendrl.dto.SongResponse;
import org.example.backendrl.service.SongService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artists/{artistId}/albums/{albumId}/songs")
@Tag(name = "Songs", description = "Songs (tracks) of an album")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    @Operation(summary = "Get songs of an album (paged, filterable)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of songs"),
            @ApiResponse(responseCode = "400", description = "Invalid filter, paging or sort parameter"),
            @ApiResponse(responseCode = "404", description = "Artist or album not found")
    })
    public PageResponse<SongResponse> getSongs(
            @PathVariable Long artistId,
            @PathVariable Long albumId,
            @Parameter(description = "Part of the song title (case-insensitive)")
            @RequestParam(required = false) String title,
            @Parameter(description = "Minimum duration in seconds")
            @RequestParam(required = false) Integer minDuration,
            @Parameter(description = "Maximum duration in seconds")
            @RequestParam(required = false) Integer maxDuration,
            @Parameter(description = "Disc number")
            @RequestParam(required = false) Integer disc,
            @ParameterObject @PageableDefault(size = 20, sort = {"discNumber", "trackNumber"}) Pageable pageable
    ) {
        return songService.getSongs(artistId, albumId, title, minDuration, maxDuration, disc, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a song of an album")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Song found"),
            @ApiResponse(responseCode = "404", description = "Artist, album or song not found")
    })
    public SongResponse getSong(
            @PathVariable Long artistId,
            @PathVariable Long albumId,
            @PathVariable Long id
    ) {
        return songService.getSong(artistId, albumId, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a song in an album")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Song created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Artist or album not found")
    })
    public SongResponse createSong(
            @PathVariable Long artistId,
            @PathVariable Long albumId,
            @Valid @RequestBody SongRequest request
    ) {
        return songService.createSong(artistId, albumId, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a song of an album")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Song updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Artist, album or song not found")
    })
    public SongResponse updateSong(
            @PathVariable Long artistId,
            @PathVariable Long albumId,
            @PathVariable Long id,
            @Valid @RequestBody SongRequest request
    ) {
        return songService.updateSong(artistId, albumId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a song of an album")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Song deleted"),
            @ApiResponse(responseCode = "404", description = "Artist, album or song not found")
    })
    public void deleteSong(
            @PathVariable Long artistId,
            @PathVariable Long albumId,
            @PathVariable Long id
    ) {
        songService.deleteSong(artistId, albumId, id);
    }
}
