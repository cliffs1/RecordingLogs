package org.example.backendrl.repository;

import org.example.backendrl.entity.SongArtist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistSongRepository extends JpaRepository<SongArtist, Long> {

    List<SongArtist> findByArtistId(Long artistId);

    List<SongArtist> findBySongId(Long songId);

    boolean existsByArtistIdAndSongId(Long artistId, Long songId);

    void deleteByArtistIdAndSongId(Long artistId, Long songId);
}
