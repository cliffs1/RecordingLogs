package org.example.backendrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendrl.dto.ArtistRequest;
import org.example.backendrl.dto.ArtistResponse;
import org.example.backendrl.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@Tag(name = "Artists", description = "Music artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    @Operation(summary = "Get all artists")
    @ApiResponse(responseCode = "200", description = "List of artists")
    public List<ArtistResponse> getAllArtists() {
        return artistService.getAllArtists();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an artist by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artist found"),
            @ApiResponse(responseCode = "404", description = "Artist not found")
    })
    public ArtistResponse getArtist(@PathVariable Long id) {
        return artistService.getArtist(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Artist created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ArtistResponse createArtist(
            @Valid @RequestBody ArtistRequest request
    ) {
        return artistService.createArtist(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artist updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Artist not found")
    })
    public ArtistResponse updateArtist(
            @PathVariable Long id,
            @Valid @RequestBody ArtistRequest request
    ) {
        return artistService.updateArtist(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Artist deleted"),
            @ApiResponse(responseCode = "404", description = "Artist not found")
    })
    public void deleteArtist(@PathVariable Long id) {
        artistService.deleteArtist(id);
    }
}
