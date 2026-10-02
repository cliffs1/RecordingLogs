package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.AlbumSummaryResponse;
import org.example.backendrl.service.AlbumArtistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@Tag(name = "Artists")
public class ArtistAlbumController {

    private final AlbumArtistService albumArtistService;

    public ArtistAlbumController(
            AlbumArtistService albumArtistService
    ) {
        this.albumArtistService = albumArtistService;
    }

    @GetMapping("/{artistId}/albums")
    @Operation(summary = "Get all albums of an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of albums"),
            @ApiResponse(responseCode = "404", description = "Artist not found")
    })
    public List<AlbumSummaryResponse> getAlbumsForArtist(
            @PathVariable Long artistId
    ) {
        return albumArtistService.getAlbumsForArtist(artistId);
    }
}
