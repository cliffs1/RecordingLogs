package org.example.backendrl.controller;

import org.example.backendrl.dto.AlbumRatingRequest;
import org.example.backendrl.dto.AlbumRatingResponse;
import org.example.backendrl.service.AlbumRatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumRatingController {

    private final AlbumRatingService albumRatingService;

    public AlbumRatingController(AlbumRatingService albumRatingService) {
        this.albumRatingService = albumRatingService;
    }

    @GetMapping("/{albumId}/ratings")
    public List<AlbumRatingResponse> getRatingsForAlbum(
            @PathVariable Long albumId) {

        return albumRatingService.getRatingsForAlbum(albumId);
    }

    @GetMapping("/{albumId}/ratings/{userId}")
    public AlbumRatingResponse getRating(
            @PathVariable Long albumId,
            @PathVariable Long userId) {

        return albumRatingService.getRating(albumId, userId);
    }

    @PostMapping("/{albumId}/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumRatingResponse createRating(
            @PathVariable Long albumId,
            @Valid @RequestBody AlbumRatingRequest request) {

        return albumRatingService.createRating(
                albumId,
                request
        );
    }

    @PutMapping("/{albumId}/ratings/{userId}")
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
    public void deleteRating(
            @PathVariable Long albumId,
            @PathVariable Long userId) {

        albumRatingService.deleteRating(
                albumId,
                userId
        );
    }
}
