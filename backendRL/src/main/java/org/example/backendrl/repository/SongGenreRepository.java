package org.example.backendrl.repository;

import org.example.backendrl.entity.SongGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SongGenreRepository extends JpaRepository<SongGenre, Long>, JpaSpecificationExecutor<SongGenre> {

    List<SongGenre> findBySongId(Long songId);

    List<SongGenre> findByGenreId(Long genreId);

    boolean existsBySongIdAndGenreId(Long songId, Long genreId);

    void deleteBySongIdAndGenreId(Long songId, Long genreId);
}
