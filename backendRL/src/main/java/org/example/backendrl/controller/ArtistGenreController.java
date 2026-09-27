package org.example.backendrl.controller;

import org.example.backendrl.dto.GenreResponse;
import org.example.backendrl.service.ArtistGenreService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistGenreController {

    private final ArtistGenreService artistGenreService;

    public ArtistGenreController(ArtistGenreService artistGenreService) {
        this.artistGenreService = artistGenreService;
    }

    @GetMapping("/{artistId}/genres")
    public List<GenreResponse> getGenresForArtist(
            @PathVariable Long artistId
    ) {
        return artistGenreService.getGenresForArtist(artistId);
    }

    @PostMapping("/{artistId}/genres/{genreId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addGenreToArtist(
            @PathVariable Long artistId,
            @PathVariable Long genreId
    ) {
        artistGenreService.addGenreToArtist(artistId, genreId);
    }

    @DeleteMapping("/{artistId}/genres/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGenreFromArtist(
            @PathVariable Long artistId,
            @PathVariable Long genreId
    ) {
        artistGenreService.removeGenreFromArtist(artistId, genreId);
    }
}