package org.example.backendrl.repository;

import org.example.backendrl.entity.SongRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SongRatingRepository extends JpaRepository<SongRating, Long> {

    List<SongRating> findBySongId(Long songId);

    List<SongRating> findByUserId(Long userId);

    Optional<SongRating> findBySongIdAndUserId(Long songId, Long userId);

    boolean existsBySongIdAndUserId(Long songId, Long userId);
}
