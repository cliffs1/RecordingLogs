package org.example.backendrl.service;

import org.example.backendrl.dto.ActivityResponse;
import org.example.backendrl.dto.UserFollowResponse;
import org.example.backendrl.entity.User;
import org.example.backendrl.entity.UserFollow;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.exception.UnprocessableEntityException;
import org.example.backendrl.repository.AlbumRatingRepository;
import org.example.backendrl.repository.AlbumReviewRepository;
import org.example.backendrl.repository.SongRatingRepository;
import org.example.backendrl.repository.UserFollowRepository;
import org.example.backendrl.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class UserFollowService {

    private static final int FEED_LIMIT = 50;

    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;
    private final AlbumRatingRepository albumRatingRepository;
    private final SongRatingRepository songRatingRepository;
    private final AlbumReviewRepository albumReviewRepository;

    public UserFollowService(
            UserFollowRepository userFollowRepository,
            UserRepository userRepository,
            AlbumRatingRepository albumRatingRepository,
            SongRatingRepository songRatingRepository,
            AlbumReviewRepository albumReviewRepository) {

        this.userFollowRepository = userFollowRepository;
        this.userRepository = userRepository;
        this.albumRatingRepository = albumRatingRepository;
        this.songRatingRepository = songRatingRepository;
        this.albumReviewRepository = albumReviewRepository;
    }

    public List<UserFollowResponse> getFollowing(Long userId) {

        checkUserExists(userId);

        return userFollowRepository.findByFollowerId(userId)
                .stream()
                .map(follow -> UserFollowResponse.of(
                        follow.getFollowed(),
                        follow.getCreatedAt()
                ))
                .toList();
    }

    public List<UserFollowResponse> getFollowers(Long userId) {

        checkUserExists(userId);

        return userFollowRepository.findByFollowedId(userId)
                .stream()
                .map(follow -> UserFollowResponse.of(
                        follow.getFollower(),
                        follow.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public UserFollowResponse follow(
            Long userId,
            Long targetId) {

        User follower = findUser(userId);
        User followed = findUser(targetId);

        if (userId.equals(targetId)) {
            throw new UnprocessableEntityException(
                    "User cannot follow themselves"
            );
        }

        if (userFollowRepository.existsByFollowerIdAndFollowedId(
                userId,
                targetId)) {

            throw new ConflictException(
                    "User " + userId + " already follows user " + targetId
            );
        }

        UserFollow follow = new UserFollow();
        follow.setFollower(follower);
        follow.setFollowed(followed);

        UserFollow savedFollow = userFollowRepository.save(follow);

        return UserFollowResponse.of(
                savedFollow.getFollowed(),
                savedFollow.getCreatedAt()
        );
    }

    @Transactional
    public void unfollow(
            Long userId,
            Long targetId) {

        UserFollow follow = userFollowRepository
                .findByFollowerIdAndFollowedId(userId, targetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User " + userId
                                        + " does not follow user " + targetId
                        ));

        userFollowRepository.delete(follow);
    }

    public List<ActivityResponse> getFeed(Long userId) {

        checkUserExists(userId);

        List<Long> followedIds = userFollowRepository.findByFollowerId(userId)
                .stream()
                .map(follow -> follow.getFollowed().getId())
                .toList();

        if (followedIds.isEmpty()) {
            return List.of();
        }

        List<ActivityResponse> activities = new ArrayList<>();

        albumRatingRepository.findByUserIdInOrderByUpdatedAtDesc(followedIds)
                .forEach(rating -> activities.add(new ActivityResponse(
                        "ALBUM_RATING",
                        rating.getUser().getId(),
                        rating.getUser().getUsername(),
                        rating.getAlbum().getId(),
                        rating.getAlbum().getTitle(),
                        String.valueOf(rating.getRating()),
                        rating.getUpdatedAt()
                )));

        songRatingRepository.findByUserIdInOrderByUpdatedAtDesc(followedIds)
                .forEach(rating -> activities.add(new ActivityResponse(
                        "SONG_RATING",
                        rating.getUser().getId(),
                        rating.getUser().getUsername(),
                        rating.getSong().getId(),
                        rating.getSong().getTitle(),
                        String.valueOf(rating.getRating()),
                        rating.getUpdatedAt()
                )));

        albumReviewRepository.findByUserIdInOrderByCreatedAtDesc(followedIds)
                .forEach(review -> activities.add(new ActivityResponse(
                        "ALBUM_REVIEW",
                        review.getUser().getId(),
                        review.getUser().getUsername(),
                        review.getAlbum().getId(),
                        review.getAlbum().getTitle(),
                        review.getTitle(),
                        review.getCreatedAt()
                )));

        return activities.stream()
                .sorted(Comparator.comparing(ActivityResponse::getTimestamp)
                        .reversed())
                .limit(FEED_LIMIT)
                .toList();
    }

    private User findUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + userId + " not found"
                        ));
    }

    private void checkUserExists(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User with id " + userId + " not found"
            );
        }
    }
}
