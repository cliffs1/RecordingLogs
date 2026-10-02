package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.SongRatingResponse;
import org.example.backendrl.service.SongRatingService;
import org.springframework.web.bind.annotation.*;

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
    public List<SongRatingResponse> getRatingsForUser(
            @PathVariable Long userId) {

        return songRatingService.getRatingsForUser(userId);
    }
}
