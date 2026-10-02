package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.AlbumSummaryResponse;
import org.example.backendrl.service.AlbumGenreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@Tag(name = "Genres")
public class GenreAlbumController {

    private final AlbumGenreService albumGenreService;

    public GenreAlbumController(
            AlbumGenreService albumGenreService
    ) {
        this.albumGenreService = albumGenreService;
    }

    @GetMapping("/{genreId}/albums")
    @Operation(summary = "Get all albums of a genre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of albums"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public List<AlbumSummaryResponse> getAlbumsForGenre(
            @PathVariable Long genreId
    ) {
        return albumGenreService.getAlbumsForGenre(genreId);
    }
}
