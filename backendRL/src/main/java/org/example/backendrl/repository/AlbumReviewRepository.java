package org.example.backendrl.repository;

import org.example.backendrl.entity.AlbumReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumReviewRepository extends JpaRepository<AlbumReview, Long> {

    List<AlbumReview> findAllByOrderByCreatedAtDesc();

    List<AlbumReview> findByAlbumIdOrderByCreatedAtDesc(Long albumId);

    List<AlbumReview> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<AlbumReview> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds);

    boolean existsByAlbumIdAndUserId(Long albumId, Long userId);

    void deleteByUserId(Long userId);

    void deleteByAlbumId(Long albumId);
}
