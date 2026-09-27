package org.example.backendrl.controller;

import org.example.backendrl.dto.AlbumSummaryResponse;
import org.example.backendrl.service.AlbumGenreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreAlbumController {

    private final AlbumGenreService albumGenreService;

    public GenreAlbumController(
            AlbumGenreService albumGenreService
    ) {
        this.albumGenreService = albumGenreService;
    }

    @GetMapping("/{genreId}/albums")
    public List<AlbumSummaryResponse> getAlbumsForGenre(
            @PathVariable Long genreId
    ) {
        return albumGenreService.getAlbumsForGenre(genreId);
    }
}
