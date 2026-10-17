package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.backendrl.dto.AlbumReviewRequest;
import org.example.backendrl.dto.AlbumReviewResponse;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.service.AlbumReviewService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
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
    public PageResponse<AlbumReviewResponse> getAllReviews(
            @Parameter(description = "Part of the review title")
            @RequestParam(required = false) String title,
            @Parameter(description = "Only reviews by this user")
            @RequestParam(required = false) Long userId,
            @Parameter(description = "Only reviews of this album")
            @RequestParam(required = false) Long albumId,
            @Parameter(description = "Written on or after this date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Written on or before this date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return albumReviewService.getAllReviews(title, userId, albumId, from, to, pageable);
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
    public PageResponse<AlbumReviewResponse> getReviewsForAlbum(
            @PathVariable Long albumId,
            @Parameter(description = "Part of the review title")
            @RequestParam(required = false) String title,
            @Parameter(description = "Only reviews by this user")
            @RequestParam(required = false) Long userId,
            @Parameter(description = "Written on or after this date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Written on or before this date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return albumReviewService.getReviewsForAlbum(albumId, title, userId, from, to, pageable);
    }

    @GetMapping("/users/{userId}/reviews")
    @Operation(summary = "Get all reviews written by a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of reviews"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public PageResponse<AlbumReviewResponse> getReviewsForUser(
            @PathVariable Long userId,
            @Parameter(description = "Part of the review title")
            @RequestParam(required = false) String title,
            @Parameter(description = "Only reviews of this album")
            @RequestParam(required = false) Long albumId,
            @Parameter(description = "Written on or after this date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Written on or before this date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return albumReviewService.getReviewsForUser(userId, title, albumId, from, to, pageable);
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
            @ApiResponse(responseCode = "403", description = "Not the author of the review")
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
