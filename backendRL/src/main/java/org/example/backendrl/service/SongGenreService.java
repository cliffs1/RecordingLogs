package org.example.backendrl.service;

import org.example.backendrl.dto.GenreResponse;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.dto.SongSummaryResponse;
import org.example.backendrl.entity.Genre;
import org.example.backendrl.entity.Song;
import org.example.backendrl.entity.SongGenre;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.GenreRepository;
import org.example.backendrl.repository.SongGenreRepository;
import org.example.backendrl.repository.SongRepository;
import org.example.backendrl.repository.spec.Specs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public PageResponse<GenreResponse> getGenresForSong(
            Long songId,
            String name,
            Pageable pageable
    ) {

        if (!songRepository.existsById(songId)) {
            throw new ResourceNotFoundException(
                    "Song with id " + songId + " not found"
            );
        }

        Specification<SongGenre> filter = Specs.all(
                Specs.equal("song.id", songId),
                Specs.contains("genre.name", name)
        );

        return PageResponse.from(
                songGenreRepository.findAll(filter, pageable),
                songGenre -> GenreResponse.fromEntity(songGenre.getGenre())
        );
    }

    public PageResponse<SongSummaryResponse> getSongsForGenre(
            Long genreId,
            String title,
            Integer minDuration,
            Integer maxDuration,
            Pageable pageable
    ) {

        if (!genreRepository.existsById(genreId)) {
            throw new ResourceNotFoundException(
                    "Genre with id " + genreId + " not found"
            );
        }

        Specification<SongGenre> filter = Specs.all(
                Specs.equal("genre.id", genreId),
                Specs.contains("song.title", title),
                Specs.atLeast("song.durationSeconds", minDuration),
                Specs.atMost("song.durationSeconds", maxDuration)
        );

        return PageResponse.from(
                songGenreRepository.findAll(filter, pageable),
                songGenre -> SongSummaryResponse.fromEntity(songGenre.getSong())
        );
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
