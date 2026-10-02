package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.AlbumFavoriteResponse;
import org.example.backendrl.service.AlbumFavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Favorites", description = "Albums marked as favorite by users")
public class AlbumFavoriteController {

    private final AlbumFavoriteService albumFavoriteService;

    public AlbumFavoriteController(AlbumFavoriteService albumFavoriteService) {
        this.albumFavoriteService = albumFavoriteService;
    }

    @GetMapping("/{userId}/favorites")
    @Operation(summary = "Get favorite albums of a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of favorite albums"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public List<AlbumFavoriteResponse> getFavorites(@PathVariable Long userId) {
        return albumFavoriteService.getFavoritesForUser(userId);
    }

    @PostMapping("/{userId}/favorites/{albumId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Mark an album as favorite")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Album added to favorites"),
            @ApiResponse(responseCode = "404", description = "User or album not found"),
            @ApiResponse(responseCode = "409", description = "Album is already a favorite")
    })
    public AlbumFavoriteResponse addFavorite(
            @PathVariable Long userId,
            @PathVariable Long albumId) {

        return albumFavoriteService.addFavorite(userId, albumId);
    }

    @DeleteMapping("/{userId}/favorites/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove an album from favorites")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Album removed from favorites"),
            @ApiResponse(responseCode = "404", description = "Album is not a favorite of the user")
    })
    public void removeFavorite(
            @PathVariable Long userId,
            @PathVariable Long albumId) {

        albumFavoriteService.removeFavorite(userId, albumId);
    }
}
