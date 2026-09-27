package org.example.backendrl.controller;

import org.example.backendrl.dto.ArtistSummaryResponse;
import org.example.backendrl.service.ArtistGenreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreArtistController {

    private final ArtistGenreService artistGenreService;

    public GenreArtistController(
            ArtistGenreService artistGenreService
    ) {
        this.artistGenreService = artistGenreService;
    }

    @GetMapping("/{genreId}/artists")
    public List<ArtistSummaryResponse> getArtistsForGenre(
            @PathVariable Long genreId
    ) {
        return artistGenreService.getArtistsForGenre(genreId);
    }
}
