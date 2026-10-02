package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.backendrl.dto.SongRatingRequest;
import org.example.backendrl.dto.SongRatingResponse;
import org.example.backendrl.service.SongRatingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@Tag(name = "Song ratings", description = "Ratings given to songs by users")
public class SongRatingController {

    private final SongRatingService songRatingService;

    public SongRatingController(SongRatingService songRatingService) {
        this.songRatingService = songRatingService;
    }

    @GetMapping("/{songId}/ratings")
    @Operation(summary = "Get all ratings of a song")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of ratings"),
            @ApiResponse(responseCode = "404", description = "Song not found")
    })
    public List<SongRatingResponse> getRatingsForSong(
            @PathVariable Long songId) {

        return songRatingService.getRatingsForSong(songId);
    }

    @GetMapping("/{songId}/ratings/{userId}")
    @Operation(summary = "Get a user's rating of a song")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rating found"),
            @ApiResponse(responseCode = "404", description = "Rating not found")
    })
    public SongRatingResponse getRating(
            @PathVariable Long songId,
            @PathVariable Long userId) {

        return songRatingService.getRating(songId, userId);
    }

    @PostMapping("/{songId}/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Rate a song")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rating created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Song or user not found"),
            @ApiResponse(responseCode = "409", description = "User already rated this song")
    })
    public SongRatingResponse createRating(
            @PathVariable Long songId,
            @Valid @RequestBody SongRatingRequest request) {

        return songRatingService.createRating(songId, request);
    }

    @PutMapping("/{songId}/ratings/{userId}")
    @Operation(summary = "Update a user's rating of a song")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rating updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Rating not found")
    })
    public SongRatingResponse updateRating(
            @PathVariable Long songId,
            @PathVariable Long userId,
            @Valid @RequestBody SongRatingRequest request) {

        return songRatingService.updateRating(
                songId,
                userId,
                request
        );
    }

    @DeleteMapping("/{songId}/ratings/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a user's rating of a song")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rating deleted"),
            @ApiResponse(responseCode = "404", description = "Rating not found")
    })
    public void deleteRating(
            @PathVariable Long songId,
            @PathVariable Long userId) {

        songRatingService.deleteRating(songId, userId);
    }
}
