package org.example.backendrl.controller;

import org.example.backendrl.dto.SongSummaryResponse;
import org.example.backendrl.service.ArtistSongService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistSongController {

    private final ArtistSongService artistSongService;

    public ArtistSongController(
            ArtistSongService artistSongService
    ) {
        this.artistSongService = artistSongService;
    }

    @GetMapping("/{artistId}/songs")
    public List<SongSummaryResponse> getSongsForArtist(
            @PathVariable Long artistId
    ) {
        return artistSongService.getSongsForArtist(artistId);
    }

    @PostMapping("/{artistId}/songs/{songId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addSongToArtist(
            @PathVariable Long artistId,
            @PathVariable Long songId
    ) {
        artistSongService.addSongToArtist(
                artistId,
                songId
        );
    }

    @DeleteMapping("/{artistId}/songs/{songId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSongFromArtist(
            @PathVariable Long artistId,
            @PathVariable Long songId
    ) {
        artistSongService.removeSongFromArtist(
                artistId,
                songId
        );
    }
}