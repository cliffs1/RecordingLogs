package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.backendrl.dto.AlbumReviewRequest;
import org.example.backendrl.dto.AlbumReviewResponse;
import org.example.backendrl.service.AlbumReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Reviews", description = "Album reviews written by users")
public class AlbumReviewController {

    private final AlbumReviewService albumReviewService;

    public AlbumReviewController(AlbumReviewService albumReviewService) {
        this.albumReviewService = albumReviewService;
    }

    @GetMapping("/reviews")
    @Operation(summary = "Get all reviews, newest first")
    @ApiResponse(responseCode = "200", description = "List of reviews")
    public List<AlbumReviewResponse> getAllReviews() {
        return albumReviewService.getAllReviews();
    }

    @GetMapping("/reviews/{reviewId}")
    @Operation(summary = "Get a review by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review found"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public AlbumReviewResponse getReview(@PathVariable Long reviewId) {
        return albumReviewService.getReview(reviewId);
    }

    @GetMapping("/albums/{albumId}/reviews")
    @Operation(summary = "Get all reviews of an album")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of reviews"),
            @ApiResponse(responseCode = "404", description = "Album not found")
    })
    public List<AlbumReviewResponse> getReviewsForAlbum(
            @PathVariable Long albumId) {

        return albumReviewService.getReviewsForAlbum(albumId);
    }

    @GetMapping("/users/{userId}/reviews")
    @Operation(summary = "Get all reviews written by a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of reviews"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public List<AlbumReviewResponse> getReviewsForUser(
            @PathVariable Long userId) {

        return albumReviewService.getReviewsForUser(userId);
    }

    @PostMapping("/albums/{albumId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Write a review for an album")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Review created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Album or user not found"),
            @ApiResponse(responseCode = "409", description = "User already reviewed this album")
    })
    public AlbumReviewResponse createReview(
            @PathVariable Long albumId,
            @Valid @RequestBody AlbumReviewRequest request) {

        return albumReviewService.createReview(albumId, request);
    }

    @PutMapping("/reviews/{reviewId}")
    @Operation(summary = "Edit a review")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "422", description = "User is not the author of the review")
    })
    public AlbumReviewResponse updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody AlbumReviewRequest request) {

        return albumReviewService.updateReview(reviewId, request);
    }

    @DeleteMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a review")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Review deleted"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public void deleteReview(@PathVariable Long reviewId) {
        albumReviewService.deleteReview(reviewId);
    }
}
