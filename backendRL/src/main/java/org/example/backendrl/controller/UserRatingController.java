package org.example.backendrl.controller;

import org.example.backendrl.dto.AlbumRatingResponse;
import org.example.backendrl.service.AlbumRatingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRatingController {

    private final AlbumRatingService albumRatingService;

    public UserRatingController(AlbumRatingService albumRatingService) {
        this.albumRatingService = albumRatingService;
    }

    @GetMapping("/{userId}/ratings")
    public List<AlbumRatingResponse> getRatingsForUser(
            @PathVariable Long userId) {

        return albumRatingService.getRatingsForUser(userId);
    }
}
