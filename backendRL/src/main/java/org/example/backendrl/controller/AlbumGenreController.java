package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.GenreResponse;
import org.example.backendrl.service.AlbumGenreService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@Tag(name = "Albums")
public class AlbumGenreController {

    private final AlbumGenreService albumGenreService;

    public AlbumGenreController(
            AlbumGenreService albumGenreService
    ) {
        this.albumGenreService = albumGenreService;
    }

    @GetMapping("/{albumId}/genres")
    @Operation(summary = "Get all genres of an album")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of genres"),
            @ApiResponse(responseCode = "404", description = "Album not found")
    })
    public List<GenreResponse> getGenresForAlbum(
            @PathVariable Long albumId
    ) {
        return albumGenreService.getGenresForAlbum(albumId);
    }

    @PostMapping("/{albumId}/genres/{genreId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a genre to an album")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Genre added to album"),
            @ApiResponse(responseCode = "404", description = "Album or genre not found"),
            @ApiResponse(responseCode = "409", description = "Album already has this genre")
    })
    public void addGenreToAlbum(
            @PathVariable Long albumId,
            @PathVariable Long genreId
    ) {
        albumGenreService.addGenreToAlbum(albumId, genreId);
    }

    @DeleteMapping("/{albumId}/genres/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a genre from an album")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Genre removed from album"),
            @ApiResponse(responseCode = "404", description = "Album, genre or link not found")
    })
    public void removeGenreFromAlbum(
            @PathVariable Long albumId,
            @PathVariable Long genreId
    ) {
        albumGenreService.removeGenreFromAlbum(albumId, genreId);
    }
}