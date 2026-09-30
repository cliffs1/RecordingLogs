package org.example.backendrl.controller;

import jakarta.validation.Valid;
import org.example.backendrl.dto.SongRatingRequest;
import org.example.backendrl.dto.SongRatingResponse;
import org.example.backendrl.service.SongRatingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongRatingController {

    private final SongRatingService songRatingService;

    public SongRatingController(SongRatingService songRatingService) {
        this.songRatingService = songRatingService;
    }

    @GetMapping("/{songId}/ratings")
    public List<SongRatingResponse> getRatingsForSong(
            @PathVariable Long songId) {

        return songRatingService.getRatingsForSong(songId);
    }

    @GetMapping("/{songId}/ratings/{userId}")
    public SongRatingResponse getRating(
            @PathVariable Long songId,
            @PathVariable Long userId) {

        return songRatingService.getRating(songId, userId);
    }

    @PostMapping("/{songId}/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    public SongRatingResponse createRating(
            @PathVariable Long songId,
            @Valid @RequestBody SongRatingRequest request) {

        return songRatingService.createRating(songId, request);
    }

    @PutMapping("/{songId}/ratings/{userId}")
    public SongRatingResponse updateRating(
            @PathVariable Long songId,
            @PathVariable Long userId,
            @Valid @RequestBody SongRatingRequest request) {

        return songRatingService.updateRating(
                songId,
                userId,
                request
        );
    }

    @DeleteMapping("/{songId}/ratings/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRating(
            @PathVariable Long songId,
            @PathVariable Long userId) {

        songRatingService.deleteRating(songId, userId);
    }
}
