package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.ArtistSummaryResponse;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.service.ArtistGenreService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

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
    public PageResponse<ArtistSummaryResponse> getArtistsForGenre(
            @PathVariable Long genreId,
            @Parameter(description = "Part of the artist name")
            @RequestParam(required = false) String name,
            @ParameterObject @PageableDefault(size = 20, sort = "artist.name") Pageable pageable
    ) {
        return artistGenreService.getArtistsForGenre(genreId, name, pageable);
    }
}
