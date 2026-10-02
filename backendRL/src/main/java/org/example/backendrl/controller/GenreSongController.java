package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.entity.Song;
import org.example.backendrl.service.SongGenreService;
import org.springframework.web.bind.annotation.*;
import org.example.backendrl.dto.SongSummaryResponse;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@Tag(name = "Genres")
public class GenreSongController {

    private final SongGenreService songGenreService;

    public GenreSongController(SongGenreService songGenreService) {
        this.songGenreService = songGenreService;
    }

    @GetMapping("/{genreId}/songs")
    @Operation(summary = "Get all songs of a genre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of songs"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public List<SongSummaryResponse> getSongsForGenre(
            @PathVariable Long genreId
    ) {
        return songGenreService.getSongsForGenre(genreId);
    }
}
