package org.example.backendrl.controller;

import org.example.backendrl.dto.AlbumSummaryResponse;
import org.example.backendrl.service.AlbumArtistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistAlbumController {

    private final AlbumArtistService albumArtistService;

    public ArtistAlbumController(
            AlbumArtistService albumArtistService
    ) {
        this.albumArtistService = albumArtistService;
    }

    @GetMapping("/{artistId}/albums")
    public List<AlbumSummaryResponse> getAlbumsForArtist(
            @PathVariable Long artistId
    ) {
        return albumArtistService.getAlbumsForArtist(artistId);
    }
}
