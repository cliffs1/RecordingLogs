package org.example.backendrl.controller;

import org.example.backendrl.dto.ArtistRequest;
import org.example.backendrl.dto.ArtistResponse;
import org.example.backendrl.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public List<ArtistResponse> getAllArtists() {
        return artistService.getAllArtists();
    }

    @GetMapping("/{id}")
    public ArtistResponse getArtist(@PathVariable Long id) {
        return artistService.getArtist(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArtistResponse createArtist(
            @Valid @RequestBody ArtistRequest request
    ) {
        return artistService.createArtist(request);
    }

    @PutMapping("/{id}")
    public ArtistResponse updateArtist(
            @PathVariable Long id,
            @Valid @RequestBody ArtistRequest request
    ) {
        return artistService.updateArtist(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArtist(@PathVariable Long id) {
        artistService.deleteArtist(id);
    }
}
