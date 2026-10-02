package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.ArtistSummaryResponse;
import org.example.backendrl.service.ArtistGenreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@Tag(name = "Genres")
public class GenreArtistController {

    private final ArtistGenreService artistGenreService;

    public GenreArtistController(
            ArtistGenreService artistGenreService
    ) {
        this.artistGenreService = artistGenreService;
    }

    @GetMapping("/{genreId}/artists")
    @Operation(summary = "Get all artists of a genre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of artists"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public List<ArtistSummaryResponse> getArtistsForGenre(
            @PathVariable Long genreId
    ) {
        return artistGenreService.getArtistsForGenre(genreId);
    }
}
