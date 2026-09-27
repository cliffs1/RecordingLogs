package org.example.backendrl.controller;

import org.example.backendrl.dto.GenreResponse;
import org.example.backendrl.service.AlbumGenreService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumGenreController {

    private final AlbumGenreService albumGenreService;

    public AlbumGenreController(
            AlbumGenreService albumGenreService
    ) {
        this.albumGenreService = albumGenreService;
    }

    @GetMapping("/{albumId}/genres")
    public List<GenreResponse> getGenresForAlbum(
            @PathVariable Long albumId
    ) {
        return albumGenreService.getGenresForAlbum(albumId);
    }

    @PostMapping("/{albumId}/genres/{genreId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addGenreToAlbum(
            @PathVariable Long albumId,
            @PathVariable Long genreId
    ) {
        albumGenreService.addGenreToAlbum(albumId, genreId);
    }

    @DeleteMapping("/{albumId}/genres/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGenreFromAlbum(
            @PathVariable Long albumId,
            @PathVariable Long genreId
    ) {
        albumGenreService.removeGenreFromAlbum(albumId, genreId);
    }
}