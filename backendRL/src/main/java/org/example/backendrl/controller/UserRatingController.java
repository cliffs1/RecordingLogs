package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.AlbumRatingResponse;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.service.AlbumRatingService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Album ratings", description = "Ratings given to albums by users")
public class UserRatingController {

    private final AlbumRatingService albumRatingService;

    public UserRatingController(AlbumRatingService albumRatingService) {
        this.albumRatingService = albumRatingService;
    }

    @GetMapping("/{userId}/ratings")
    @Operation(summary = "Get all album ratings given by a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of ratings"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public PageResponse<AlbumRatingResponse> getRatingsForUser(
            @PathVariable Long userId,
            @Parameter(description = "Only ratings of this album")
            @RequestParam(required = false) Long albumId,
            @Parameter(description = "Minimum rating (1-10)")
            @RequestParam(required = false) Integer minRating,
            @Parameter(description = "Maximum rating (1-10)")
            @RequestParam(required = false) Integer maxRating,
            @Parameter(description = "Rated on or after this date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Rated on or before this date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @ParameterObject @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return albumRatingService.getRatingsForUser(userId, albumId, minRating, maxRating, from, to, pageable);
    }
}
