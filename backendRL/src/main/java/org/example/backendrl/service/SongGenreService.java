package org.example.backendrl.service;

import org.example.backendrl.entity.Genre;
import org.example.backendrl.entity.Song;
import org.example.backendrl.entity.SongGenre;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.GenreRepository;
import org.example.backendrl.repository.SongGenreRepository;
import org.example.backendrl.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.backendrl.dto.SongSummaryResponse;

import java.util.List;

@Service
public class SongGenreService {

    private final SongGenreRepository songGenreRepository;
    private final SongRepository songRepository;
    private final GenreRepository genreRepository;

    public SongGenreService(
            SongGenreRepository songGenreRepository,
            SongRepository songRepository,
            GenreRepository genreRepository
    ) {
        this.songGenreRepository = songGenreRepository;
        this.songRepository = songRepository;
        this.genreRepository = genreRepository;
    }

    public List<Genre> getGenresForSong(Long songId) {

        if (!songRepository.existsById(songId)) {
            throw new ResourceNotFoundException(
                    "Song with id " + songId + " not found"
            );
        }

        return songGenreRepository.findBySongId(songId)
                .stream()
                .map(SongGenre::getGenre)
                .toList();
    }

    public List<SongSummaryResponse> getSongsForGenre(Long genreId) {

        if (!genreRepository.existsById(genreId)) {
            throw new ResourceNotFoundException(
                    "Genre with id " + genreId + " not found"
            );
        }

        return songGenreRepository.findByGenreId(genreId)
                .stream()
                .map(SongGenre::getSong)
                .map(SongSummaryResponse::fromEntity)
                .toList();
    }

    public void addGenreToSong(Long songId, Long genreId) {

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song with id " + songId + " not found"
                ));

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Genre with id " + genreId + " not found"
                ));

        if (songGenreRepository.existsBySongIdAndGenreId(songId, genreId)) {
            throw new ConflictException(
                    "Song " + songId + " is already associated with genre " + genreId
            );
        }

        SongGenre songGenre = new SongGenre();
        songGenre.setSong(song);
        songGenre.setGenre(genre);

        songGenreRepository.save(songGenre);
    }

    @Transactional
    public void removeGenreFromSong(Long songId, Long genreId) {

        if (!songRepository.existsById(songId)) {
            throw new ResourceNotFoundException(
                    "Song with id " + songId + " not found"
            );
        }

        if (!genreRepository.existsById(genreId)) {
            throw new ResourceNotFoundException(
                    "Genre with id " + genreId + " not found"
            );
        }

        if (!songGenreRepository.existsBySongIdAndGenreId(songId, genreId)) {
            throw new ResourceNotFoundException(
                    "Genre " + genreId + " is not associated with song " + songId
            );
        }

        songGenreRepository.deleteBySongIdAndGenreId(songId, genreId);
    }
}
