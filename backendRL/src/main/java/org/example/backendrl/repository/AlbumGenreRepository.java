package org.example.backendrl.repository;

import org.example.backendrl.entity.AlbumGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AlbumGenreRepository extends JpaRepository<AlbumGenre, Long>, JpaSpecificationExecutor<AlbumGenre> {

    List<AlbumGenre> findByAlbumId(Long albumId);

    List<AlbumGenre> findByGenreId(Long genreId);

    boolean existsByAlbumIdAndGenreId(Long albumId, Long genreId);

    void deleteByAlbumIdAndGenreId(Long albumId, Long genreId);
}