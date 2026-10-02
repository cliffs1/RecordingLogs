package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.AlbumRatingResponse;
import org.example.backendrl.service.AlbumRatingService;
import org.springframework.web.bind.annotation.*;

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
    public List<AlbumRatingResponse> getRatingsForUser(
            @PathVariable Long userId) {

        return albumRatingService.getRatingsForUser(userId);
    }
}
