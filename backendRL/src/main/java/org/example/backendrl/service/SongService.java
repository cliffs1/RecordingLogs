package org.example.backendrl.service;

import org.example.backendrl.dto.SongRequest;
import org.example.backendrl.dto.SongResponse;
import org.example.backendrl.entity.Song;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongService {

    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public List<SongResponse> getAllSongs() {
        return songRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SongResponse getSong(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song with id " + id + " not found"
                ));

        return toResponse(song);
    }

    public SongResponse createSong(SongRequest request) {
        Song song = new Song();

        song.setTitle(request.getTitle());
        song.setDurationSeconds(request.getDurationSeconds());
        song.setOriginalReleaseDate(request.getOriginalReleaseDate());

        Song savedSong = songRepository.save(song);

        return toResponse(savedSong);
    }

    public SongResponse updateSong(Long id, SongRequest request) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song with id " + id + " not found"
                ));

        song.setTitle(request.getTitle());
        song.setDurationSeconds(request.getDurationSeconds());
        song.setOriginalReleaseDate(request.getOriginalReleaseDate());

        Song updatedSong = songRepository.save(song);

        return toResponse(updatedSong);
    }

    public void deleteSong(Long id) {
        if (!songRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Song with id " + id + " not found"
            );
        }

        songRepository.deleteById(id);
    }

    private SongResponse toResponse(Song song) {
        return new SongResponse(
                song.getId(),
                song.getTitle(),
                song.getDurationSeconds(),
                song.getOriginalReleaseDate()
        );
    }
}