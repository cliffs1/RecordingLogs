package org.example.backendrl.repository;

import org.example.backendrl.entity.AlbumFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumFavoriteRepository extends JpaRepository<AlbumFavorite, Long> {

    List<AlbumFavorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<AlbumFavorite> findByUserIdAndAlbumId(Long userId, Long albumId);

    boolean existsByUserIdAndAlbumId(Long userId, Long albumId);

    long countByAlbumId(Long albumId);

    void deleteByUserId(Long userId);

    void deleteByAlbumId(Long albumId);
}
