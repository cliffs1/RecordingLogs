package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.SongRequest;
import org.example.backendrl.dto.SongResponse;
import org.example.backendrl.service.SongService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@Tag(name = "Songs", description = "Songs (tracks)")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    @Operation(summary = "Get all songs")
    @ApiResponse(responseCode = "200", description = "List of songs")
    public List<SongResponse> getAllSongs() {
        return songService.getAllSongs();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a song by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Song found"),
            @ApiResponse(responseCode = "404", description = "Song not found")
    })
    public SongResponse getSong(@PathVariable Long id) {
        return songService.getSong(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a song")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Song created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public SongResponse createSong(
            @Valid @RequestBody SongRequest request
    ) {
        return songService.createSong(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a song")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Song updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Song not found")
    })
    public SongResponse updateSong(
            @PathVariable Long id,
            @Valid @RequestBody SongRequest request
    ) {
        return songService.updateSong(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a song")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Song deleted"),
            @ApiResponse(responseCode = "404", description = "Song not found")
    })
    public void deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
    }
}
