package org.example.backendrl.controller;

import org.example.backendrl.entity.Song;
import org.example.backendrl.service.SongGenreService;
import org.springframework.web.bind.annotation.*;
import org.example.backendrl.dto.SongSummaryResponse;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreSongController {

    private final SongGenreService songGenreService;

    public GenreSongController(SongGenreService songGenreService) {
        this.songGenreService = songGenreService;
    }

    @GetMapping("/{genreId}/songs")
    public List<SongSummaryResponse> getSongsForGenre(
            @PathVariable Long genreId
    ) {
        return songGenreService.getSongsForGenre(genreId);
    }
}
