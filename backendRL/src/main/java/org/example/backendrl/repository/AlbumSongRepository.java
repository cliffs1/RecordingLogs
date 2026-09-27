package org.example.backendrl.repository;

import org.example.backendrl.entity.AlbumSong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumSongRepository extends JpaRepository<AlbumSong, Long> {

    List<AlbumSong> findByAlbumIdOrderByDiscNumberAscTrackNumberAsc(Long albumId);

    List<AlbumSong> findBySongId(Long songId);

    Optional<AlbumSong> findByAlbumIdAndSongId(Long albumId, Long songId);

    boolean existsByAlbumIdAndSongId(Long albumId, Long songId);
}
