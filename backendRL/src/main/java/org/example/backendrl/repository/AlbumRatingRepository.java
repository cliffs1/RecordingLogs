package org.example.backendrl.repository;

import org.example.backendrl.entity.AlbumRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRatingRepository extends JpaRepository<AlbumRating, Long> {

    List<AlbumRating> findByAlbumId(Long albumId);

    List<AlbumRating> findByUserId(Long userId);

    Optional<AlbumRating> findByAlbumIdAndUserId(Long albumId, Long userId);

    boolean existsByAlbumIdAndUserId(Long albumId, Long userId);
}
