package org.example.backendrl.service;

import org.example.backendrl.dto.ArtistSummaryResponse;
import org.example.backendrl.dto.GenreResponse;
import org.example.backendrl.entity.Artist;
import org.example.backendrl.entity.ArtistGenre;
import org.example.backendrl.entity.Genre;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.ArtistGenreRepository;
import org.example.backendrl.repository.ArtistRepository;
import org.example.backendrl.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArtistGenreService {

    private final ArtistGenreRepository artistGenreRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;

    public ArtistGenreService(
            ArtistGenreRepository artistGenreRepository,
            ArtistRepository artistRepository,
            GenreRepository genreRepository
    ) {
        this.artistGenreRepository = artistGenreRepository;
        this.artistRepository = artistRepository;
        this.genreRepository = genreRepository;
    }

    public List<GenreResponse> getGenresForArtist(Long artistId) {
        if (!artistRepository.existsById(artistId)) {
            throw new ResourceNotFoundException(
                    "Artist with id " + artistId + " not found"
            );
        }

        return artistGenreRepository.findByArtistId(artistId)
                .stream()
                .map(ArtistGenre::getGenre)
                .map(GenreResponse::fromEntity)
                .toList();
    }

    public void addGenreToArtist(Long artistId, Long genreId) {

        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artist with id " + artistId + " not found"
                ));

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Genre with id " + genreId + " not found"
                ));

        if (artistGenreRepository.existsByArtistIdAndGenreId(
                artistId, genreId)) {
            return;
        }

        ArtistGenre artistGenre = new ArtistGenre();
        artistGenre.setArtist(artist);
        artistGenre.setGenre(genre);

        artistGenreRepository.save(artistGenre);
    }

    @Transactional
    public void removeGenreFromArtist(Long artistId, Long genreId) {

        if (!artistRepository.existsById(artistId)) {
            throw new ResourceNotFoundException(
                    "Artist with id " + artistId + " not found"
            );
        }

        if (!genreRepository.existsById(genreId)) {
            throw new ResourceNotFoundException(
                    "Genre with id " + genreId + " not found"
            );
        }

        if (!artistGenreRepository.existsByArtistIdAndGenreId(
                artistId, genreId)) {

            throw new ResourceNotFoundException(
                    "Genre " + genreId +
                            " is not associated with artist " + artistId
            );
        }

        artistGenreRepository.deleteByArtistIdAndGenreId(
                artistId, genreId
        );
    }

    public List<ArtistSummaryResponse> getArtistsForGenre(Long genreId) {

        if (!genreRepository.existsById(genreId)) {
            throw new ResourceNotFoundException(
                    "Genre with id " + genreId + " not found"
            );
        }

        return artistGenreRepository.findByGenreId(genreId)
                .stream()
                .map(ArtistGenre::getArtist)
                .map(ArtistSummaryResponse::fromEntity)
                .toList();
    }
}
