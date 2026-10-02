package org.example.backendrl.repository;

import org.example.backendrl.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    List<UserFollow> findByFollowerId(Long followerId);

    List<UserFollow> findByFollowedId(Long followedId);

    Optional<UserFollow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);

    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);

    void deleteByFollowerId(Long followerId);

    void deleteByFollowedId(Long followedId);
}
