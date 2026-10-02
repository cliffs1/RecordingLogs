package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.AlbumRequest;
import org.example.backendrl.dto.AlbumResponse;
import org.example.backendrl.service.AlbumService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@Tag(name = "Albums", description = "Music albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    @Operation(summary = "Get all albums")
    @ApiResponse(responseCode = "200", description = "List of albums")
    public List<AlbumResponse> getAllAlbums() {
        return albumService.getAllAlbums();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an album by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album found"),
            @ApiResponse(responseCode = "404", description = "Album not found")
    })
    public AlbumResponse getAlbum(
            @PathVariable Long id
    ) {
        return albumService.getAlbum(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an album")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Album created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public AlbumResponse createAlbum(
            @Valid @RequestBody AlbumRequest request
    ) {
        return albumService.createAlbum(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an album")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Album not found")
    })
    public AlbumResponse updateAlbum(
            @PathVariable Long id,
            @Valid @RequestBody AlbumRequest request
    ) {
        return albumService.updateAlbum(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an album")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Album deleted"),
            @ApiResponse(responseCode = "404", description = "Album not found")
    })
    public void deleteAlbum(
            @PathVariable Long id
    ) {
        albumService.deleteAlbum(id);
    }
}
