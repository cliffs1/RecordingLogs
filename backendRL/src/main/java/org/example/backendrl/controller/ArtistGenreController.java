package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.GenreResponse;
import org.example.backendrl.service.ArtistGenreService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@Tag(name = "Artists")
public class ArtistGenreController {

    private final ArtistGenreService artistGenreService;

    public ArtistGenreController(ArtistGenreService artistGenreService) {
        this.artistGenreService = artistGenreService;
    }

    @GetMapping("/{artistId}/genres")
    @Operation(summary = "Get all genres of an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of genres"),
            @ApiResponse(responseCode = "404", description = "Artist not found")
    })
    public List<GenreResponse> getGenresForArtist(
            @PathVariable Long artistId
    ) {
        return artistGenreService.getGenresForArtist(artistId);
    }

    @PostMapping("/{artistId}/genres/{genreId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a genre to an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Genre added to artist"),
            @ApiResponse(responseCode = "404", description = "Artist or genre not found"),
            @ApiResponse(responseCode = "409", description = "Artist already has this genre")
    })
    public void addGenreToArtist(
            @PathVariable Long artistId,
            @PathVariable Long genreId
    ) {
        artistGenreService.addGenreToArtist(artistId, genreId);
    }

    @DeleteMapping("/{artistId}/genres/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a genre from an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Genre removed from artist"),
            @ApiResponse(responseCode = "404", description = "Artist, genre or link not found")
    })
    public void removeGenreFromArtist(
            @PathVariable Long artistId,
            @PathVariable Long genreId
    ) {
        artistGenreService.removeGenreFromArtist(artistId, genreId);
    }
}