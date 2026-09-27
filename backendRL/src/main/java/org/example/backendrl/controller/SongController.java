package org.example.backendrl.controller;

import org.example.backendrl.dto.SongRequest;
import org.example.backendrl.dto.SongResponse;
import org.example.backendrl.service.SongService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    public List<SongResponse> getAllSongs() {
        return songService.getAllSongs();
    }

    @GetMapping("/{id}")
    public SongResponse getSong(@PathVariable Long id) {
        return songService.getSong(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SongResponse createSong(
            @Valid @RequestBody SongRequest request
    ) {
        return songService.createSong(request);
    }

    @PutMapping("/{id}")
    public SongResponse updateSong(
            @PathVariable Long id,
            @Valid @RequestBody SongRequest request
    ) {
        return songService.updateSong(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
    }
}
