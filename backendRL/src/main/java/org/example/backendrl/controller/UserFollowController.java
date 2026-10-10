package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.ActivityResponse;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.dto.UserFollowResponse;
import org.example.backendrl.service.UserFollowService;
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
@RequestMapping("/api/users")
@Tag(name = "Follows", description = "Following other users and their activity")
public class UserFollowController {

    private final UserFollowService userFollowService;

    public UserFollowController(UserFollowService userFollowService) {
        this.userFollowService = userFollowService;
    }

    @GetMapping("/{userId}/following")
    @Operation(summary = "Get users followed by a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of followed users"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public PageResponse<UserFollowResponse> getFollowing(
            @PathVariable Long userId,
            @Parameter(description = "Part of the followed user's username")
            @RequestParam(required = false) String username,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return userFollowService.getFollowing(userId, username, pageable);
    }

    @GetMapping("/{userId}/followers")
    @Operation(summary = "Get followers of a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of followers"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public PageResponse<UserFollowResponse> getFollowers(
            @PathVariable Long userId,
            @Parameter(description = "Part of the follower's username")
            @RequestParam(required = false) String username,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return userFollowService.getFollowers(userId, username, pageable);
    }

    @PostMapping("/{userId}/following/{targetId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Follow another user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User followed"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Already following this user"),
            @ApiResponse(responseCode = "422", description = "User cannot follow themselves")
    })
    public UserFollowResponse follow(
            @PathVariable Long userId,
            @PathVariable Long targetId) {

        return userFollowService.follow(userId, targetId);
    }

    @DeleteMapping("/{userId}/following/{targetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Unfollow a user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User unfollowed"),
            @ApiResponse(responseCode = "404", description = "User does not follow the target")
    })
    public void unfollow(
            @PathVariable Long userId,
            @PathVariable Long targetId) {

        userFollowService.unfollow(userId, targetId);
    }

    @GetMapping("/{userId}/feed")
    @Operation(summary = "Get recent activity of followed users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ratings and reviews of followed users, newest first"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public PageResponse<ActivityResponse> getFeed(
            @PathVariable Long userId,
            @Parameter(description = "Activity type: ALBUM_RATING, SONG_RATING or ALBUM_REVIEW")
            @RequestParam(required = false) String type,
            @Parameter(description = "Part of the username of the followed user")
            @RequestParam(required = false) String username,
            @Parameter(description = "Happened on or after this date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Happened on or before this date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        return userFollowService.getFeed(userId, type, username, from, to, pageable);
    }
}
