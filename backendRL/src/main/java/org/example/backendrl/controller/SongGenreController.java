package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.entity.Genre;
import org.example.backendrl.service.SongGenreService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@Tag(name = "Songs")
public class SongGenreController {

    private final SongGenreService songGenreService;

    public SongGenreController(SongGenreService songGenreService) {
        this.songGenreService = songGenreService;
    }

    @GetMapping("/{songId}/genres")
    @Operation(summary = "Get all genres of a song")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of genres"),
            @ApiResponse(responseCode = "404", description = "Song not found")
    })
    public List<Genre> getGenresForSong(
            @PathVariable Long songId
    ) {
        return songGenreService.getGenresForSong(songId);
    }

    @PostMapping("/{songId}/genres/{genreId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a genre to a song")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Genre added to song"),
            @ApiResponse(responseCode = "404", description = "Song or genre not found"),
            @ApiResponse(responseCode = "409", description = "Song already has this genre")
    })
    public void addGenreToSong(
            @PathVariable Long songId,
            @PathVariable Long genreId
    ) {
        songGenreService.addGenreToSong(songId, genreId);
    }

    @DeleteMapping("/{songId}/genres/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a genre from a song")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Genre removed from song"),
            @ApiResponse(responseCode = "404", description = "Song, genre or link not found")
    })
    public void removeGenreFromSong(
            @PathVariable Long songId,
            @PathVariable Long genreId
    ) {
        songGenreService.removeGenreFromSong(songId, genreId);
    }
}
