package org.example.backendrl.repository;

import org.example.backendrl.entity.ArtistGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistGenreRepository extends JpaRepository<ArtistGenre, Long> {

    List<ArtistGenre> findByArtistId(Long artistId);

    List<ArtistGenre> findByGenreId(Long genreId);

    boolean existsByArtistIdAndGenreId(Long artistId, Long genreId);

    void deleteByArtistIdAndGenreId(Long artistId, Long genreId);
}
