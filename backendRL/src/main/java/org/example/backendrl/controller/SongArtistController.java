package org.example.backendrl.controller;

import org.example.backendrl.dto.ArtistSummaryResponse;
import org.example.backendrl.service.ArtistSongService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongArtistController {

    private final ArtistSongService artistSongService;

    public SongArtistController(
            ArtistSongService artistSongService
    ) {
        this.artistSongService = artistSongService;
    }

    @GetMapping("/{songId}/artists")
    public List<ArtistSummaryResponse> getArtistsForSong(
            @PathVariable Long songId
    ) {
        return artistSongService.getArtistsForSong(songId);
    }
}
