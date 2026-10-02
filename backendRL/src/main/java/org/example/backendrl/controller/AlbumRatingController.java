package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.AlbumRatingRequest;
import org.example.backendrl.dto.AlbumRatingResponse;
import org.example.backendrl.service.AlbumRatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@Tag(name = "Album ratings", description = "Ratings given to albums by users")
public class AlbumRatingController {

    private final AlbumRatingService albumRatingService;

    public AlbumRatingController(AlbumRatingService albumRatingService) {
        this.albumRatingService = albumRatingService;
    }

    @GetMapping("/{albumId}/ratings")
    @Operation(summary = "Get all ratings of an album")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of ratings"),
            @ApiResponse(responseCode = "404", description = "Album not found")
    })
    public List<AlbumRatingResponse> getRatingsForAlbum(
            @PathVariable Long albumId) {

        return albumRatingService.getRatingsForAlbum(albumId);
    }

    @GetMapping("/{albumId}/ratings/{userId}")
    @Operation(summary = "Get a user's rating of an album")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rating found"),
            @ApiResponse(responseCode = "404", description = "Rating not found")
    })
    public AlbumRatingResponse getRating(
            @PathVariable Long albumId,
            @PathVariable Long userId) {

        return albumRatingService.getRating(albumId, userId);
    }

    @PostMapping("/{albumId}/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Rate an album")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rating created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Album or user not found"),
            @ApiResponse(responseCode = "409", description = "User already rated this album")
    })
    public AlbumRatingResponse createRating(
            @PathVariable Long albumId,
            @Valid @RequestBody AlbumRatingRequest request) {

        return albumRatingService.createRating(
                albumId,
                request
        );
    }

    @PutMapping("/{albumId}/ratings/{userId}")
    @Operation(summary = "Update a user's rating of an album")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rating updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Rating not found")
    })
    public AlbumRatingResponse updateRating(
            @PathVariable Long albumId,
            @PathVariable Long userId,
            @Valid @RequestBody AlbumRatingRequest request) {

        return albumRatingService.updateRating(
                albumId,
                userId,
                request
        );
    }

    @DeleteMapping("/{albumId}/ratings/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a user's rating of an album")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rating deleted"),
            @ApiResponse(responseCode = "404", description = "Rating not found")
    })
    public void deleteRating(
            @PathVariable Long albumId,
            @PathVariable Long userId) {

        albumRatingService.deleteRating(
                albumId,
                userId
        );
    }
}
