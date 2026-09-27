package org.example.backendrl.service;

import org.example.backendrl.dto.GenreRequest;
import org.example.backendrl.dto.GenreResponse;
import org.example.backendrl.entity.Genre;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<GenreResponse> getAllGenres() {
        return genreRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public GenreResponse getGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Genre with id " + id + " not found"
                ));

        return toResponse(genre);
    }

    public GenreResponse createGenre(GenreRequest request) {
        Genre genre = new Genre();
        genre.setName(request.getName());

        Genre savedGenre = genreRepository.save(genre);

        return toResponse(savedGenre);
    }

    public GenreResponse updateGenre(Long id, GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Genre with id " + id + " not found"
                ));

        genre.setName(request.getName());

        Genre updatedGenre = genreRepository.save(genre);

        return toResponse(updatedGenre);
    }

    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Genre with id " + id + " not found"
            );
        }

        genreRepository.deleteById(id);
    }

    private GenreResponse toResponse(Genre genre) {
        return new GenreResponse(
                genre.getId(),
                genre.getName()
        );
    }
}
