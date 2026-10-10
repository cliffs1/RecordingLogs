package org.example.backendrl.service;

import org.example.backendrl.dto.AlbumSummaryResponse;
import org.example.backendrl.dto.GenreResponse;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.entity.Album;
import org.example.backendrl.entity.AlbumGenre;
import org.example.backendrl.entity.Genre;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.AlbumGenreRepository;
import org.example.backendrl.repository.AlbumRepository;
import org.example.backendrl.repository.GenreRepository;
import org.example.backendrl.repository.spec.Specs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AlbumGenreService {

    private final AlbumGenreRepository albumGenreRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;

    public AlbumGenreService(
            AlbumGenreRepository albumGenreRepository,
            AlbumRepository albumRepository,
            GenreRepository genreRepository
    ) {
        this.albumGenreRepository = albumGenreRepository;
        this.albumRepository = albumRepository;
        this.genreRepository = genreRepository;
    }

    public PageResponse<GenreResponse> getGenresForAlbum(
            Long albumId,
            String name,
            Pageable pageable
    ) {

        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException(
                    "Album with id " + albumId + " not found"
            );
        }

        Specification<AlbumGenre> filter = Specs.all(
                Specs.equal("album.id", albumId),
                Specs.contains("genre.name", name)
        );

        return PageResponse.from(
                albumGenreRepository.findAll(filter, pageable),
                albumGenre -> GenreResponse.fromEntity(albumGenre.getGenre())
        );
    }

    public void addGenreToAlbum(Long albumId, Long genreId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Album with id " + albumId + " not found"
                ));

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Genre with id " + genreId + " not found"
                ));

        if (albumGenreRepository.existsByAlbumIdAndGenreId(
                albumId, genreId)) {
            throw new ConflictException(
                    "Album " + albumId + " is already associated with genre " + genreId
            );
        }

        AlbumGenre albumGenre = new AlbumGenre();
        albumGenre.setAlbum(album);
        albumGenre.setGenre(genre);

        albumGenreRepository.save(albumGenre);
    }

    @Transactional
    public void removeGenreFromAlbum(Long albumId, Long genreId) {

        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException(
                    "Album with id " + albumId + " not found"
            );
        }

        if (!genreRepository.existsById(genreId)) {
            throw new ResourceNotFoundException(
                    "Genre with id " + genreId + " not found"
            );
        }

        if (!albumGenreRepository.existsByAlbumIdAndGenreId(
                albumId, genreId)) {

            throw new ResourceNotFoundException(
                    "Genre " + genreId +
                            " is not associated with album " + albumId
            );
        }

        albumGenreRepository.deleteByAlbumIdAndGenreId(
                albumId, genreId
        );
    }

    public PageResponse<AlbumSummaryResponse> getAlbumsForGenre(
            Long genreId,
            String title,
            LocalDate releasedFrom,
            LocalDate releasedTo,
            Pageable pageable
    ) {

        if (!genreRepository.existsById(genreId)) {
            throw new ResourceNotFoundException(
                    "Genre with id " + genreId + " not found"
            );
        }

        Specification<AlbumGenre> filter = Specs.all(
                Specs.equal("genre.id", genreId),
                Specs.contains("album.title", title),
                Specs.atLeast("album.releaseDate", releasedFrom),
                Specs.atMost("album.releaseDate", releasedTo)
        );

        return PageResponse.from(
                albumGenreRepository.findAll(filter, pageable),
                albumGenre -> AlbumSummaryResponse.fromEntity(albumGenre.getAlbum())
        );
    }
}
