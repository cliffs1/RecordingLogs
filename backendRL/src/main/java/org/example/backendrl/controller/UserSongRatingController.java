package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.dto.SongRatingResponse;
import org.example.backendrl.service.SongRatingService;
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
@Tag(name = "Song ratings", description = "Ratings given to songs by users")
public class UserSongRatingController {

    private final SongRatingService songRatingService;

    public UserSongRatingController(SongRatingService songRatingService) {
        this.songRatingService = songRatingService;
    }

    @GetMapping("/{userId}/song-ratings")
    @Operation(summary = "Get all song ratings given by a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of ratings"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public PageResponse<SongRatingResponse> getRatingsForUser(
            @PathVariable Long userId,
            @Parameter(description = "Only ratings of this song")
            @RequestParam(required = false) Long songId,
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
        return songRatingService.getRatingsForUser(userId, songId, minRating, maxRating, from, to, pageable);
    }
}
