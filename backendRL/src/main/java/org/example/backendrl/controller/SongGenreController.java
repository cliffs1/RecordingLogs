package org.example.backendrl.controller;

import org.example.backendrl.entity.Genre;
import org.example.backendrl.service.SongGenreService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongGenreController {

    private final SongGenreService songGenreService;

    public SongGenreController(SongGenreService songGenreService) {
        this.songGenreService = songGenreService;
    }

    @GetMapping("/{songId}/genres")
    public List<Genre> getGenresForSong(
            @PathVariable Long songId
    ) {
        return songGenreService.getGenresForSong(songId);
    }

    @PostMapping("/{songId}/genres/{genreId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addGenreToSong(
            @PathVariable Long songId,
            @PathVariable Long genreId
    ) {
        songGenreService.addGenreToSong(songId, genreId);
    }

    @DeleteMapping("/{songId}/genres/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGenreFromSong(
            @PathVariable Long songId,
            @PathVariable Long genreId
    ) {
        songGenreService.removeGenreFromSong(songId, genreId);
    }
}
