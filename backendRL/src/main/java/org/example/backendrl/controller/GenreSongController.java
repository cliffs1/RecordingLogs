package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.dto.SongSummaryResponse;
import org.example.backendrl.service.SongGenreService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

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
    public PageResponse<SongSummaryResponse> getSongsForGenre(
            @PathVariable Long genreId,
            @Parameter(description = "Part of the song title")
            @RequestParam(required = false) String title,
            @Parameter(description = "Minimum duration in seconds")
            @RequestParam(required = false) Integer minDuration,
            @Parameter(description = "Maximum duration in seconds")
            @RequestParam(required = false) Integer maxDuration,
            @ParameterObject @PageableDefault(size = 20, sort = "song.title") Pageable pageable
    ) {
        return songGenreService.getSongsForGenre(genreId, title, minDuration, maxDuration, pageable);
    }
}
