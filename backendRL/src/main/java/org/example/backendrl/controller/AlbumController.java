package org.example.backendrl.controller;

import org.example.backendrl.dto.AlbumRequest;
import org.example.backendrl.dto.AlbumResponse;
import org.example.backendrl.service.AlbumService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public List<AlbumResponse> getAllAlbums() {
        return albumService.getAllAlbums();
    }

    @GetMapping("/{id}")
    public AlbumResponse getAlbum(
            @PathVariable Long id
    ) {
        return albumService.getAlbum(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumResponse createAlbum(
            @Valid @RequestBody AlbumRequest request
    ) {
        return albumService.createAlbum(request);
    }

    @PutMapping("/{id}")
    public AlbumResponse updateAlbum(
            @PathVariable Long id,
            @Valid @RequestBody AlbumRequest request
    ) {
        return albumService.updateAlbum(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbum(
            @PathVariable Long id
    ) {
        albumService.deleteAlbum(id);
    }
}
