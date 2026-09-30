package org.example.backendrl.controller;

import org.example.backendrl.dto.SongRatingResponse;
import org.example.backendrl.service.SongRatingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserSongRatingController {

    private final SongRatingService songRatingService;

    public UserSongRatingController(SongRatingService songRatingService) {
        this.songRatingService = songRatingService;
    }

    @GetMapping("/{userId}/song-ratings")
    public List<SongRatingResponse> getRatingsForUser(
            @PathVariable Long userId) {

        return songRatingService.getRatingsForUser(userId);
    }
}
