package org.example.backendrl.service;

import org.example.backendrl.dto.ArtistSummaryResponse;
import org.example.backendrl.dto.GenreResponse;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.entity.Artist;
import org.example.backendrl.entity.ArtistGenre;
import org.example.backendrl.entity.Genre;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.ArtistGenreRepository;
import org.example.backendrl.repository.ArtistRepository;
import org.example.backendrl.repository.GenreRepository;
import org.example.backendrl.repository.spec.Specs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public PageResponse<GenreResponse> getGenresForArtist(
            Long artistId,
            String name,
            Pageable pageable
    ) {

        if (!artistRepository.existsById(artistId)) {
            throw new ResourceNotFoundException(
                    "Artist with id " + artistId + " not found"
            );
        }

        Specification<ArtistGenre> filter = Specs.all(
                Specs.equal("artist.id", artistId),
                Specs.contains("genre.name", name)
        );

        return PageResponse.from(
                artistGenreRepository.findAll(filter, pageable),
                artistGenre -> GenreResponse.fromEntity(artistGenre.getGenre())
        );
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
            throw new ConflictException(
                    "Artist " + artistId + " is already associated with genre " +genreId
            );
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

    public PageResponse<ArtistSummaryResponse> getArtistsForGenre(
            Long genreId,
            String name,
            Pageable pageable
    ) {

        if (!genreRepository.existsById(genreId)) {
            throw new ResourceNotFoundException(
                    "Genre with id " + genreId + " not found"
            );
        }

        Specification<ArtistGenre> filter = Specs.all(
                Specs.equal("genre.id", genreId),
                Specs.contains("artist.name", name)
        );

        return PageResponse.from(
                artistGenreRepository.findAll(filter, pageable),
                artistGenre -> ArtistSummaryResponse.fromEntity(artistGenre.getArtist())
        );
    }
}
