package org.example.backendrl.repository;

import org.example.backendrl.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long>, JpaSpecificationExecutor<Song> {

    Optional<Song> findByIdAndAlbumId(Long id, Long albumId);
}
