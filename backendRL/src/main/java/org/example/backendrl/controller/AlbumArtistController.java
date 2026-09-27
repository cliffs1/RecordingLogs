package org.example.backendrl.controller;

import org.example.backendrl.dto.ArtistSummaryResponse;
import org.example.backendrl.service.AlbumArtistService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumArtistController {

    private final AlbumArtistService albumArtistService;

    public AlbumArtistController(
            AlbumArtistService albumArtistService
    ) {
        this.albumArtistService = albumArtistService;
    }

    @GetMapping("/{albumId}/artists")
    public List<ArtistSummaryResponse> getArtistsForAlbum(
            @PathVariable Long albumId
    ) {
        return albumArtistService.getArtistsForAlbum(albumId);
    }

    @PostMapping("/{albumId}/artists/{artistId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addArtistToAlbum(
            @PathVariable Long albumId,
            @PathVariable Long artistId
    ) {
        albumArtistService.addArtistToAlbum(albumId, artistId);
    }

    @DeleteMapping("/{albumId}/artists/{artistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeArtistFromAlbum(
            @PathVariable Long albumId,
            @PathVariable Long artistId
    ) {
        albumArtistService.removeArtistFromAlbum(albumId, artistId);
    }
}
