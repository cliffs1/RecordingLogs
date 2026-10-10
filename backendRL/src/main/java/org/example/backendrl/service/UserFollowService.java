package org.example.backendrl.service;

import org.example.backendrl.dto.ActivityResponse;
import org.example.backendrl.dto.PageResponse;
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
import org.example.backendrl.repository.spec.Specs;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@Service
public class UserFollowService {

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

    public PageResponse<UserFollowResponse> getFollowing(
            Long userId,
            String username,
            Pageable pageable
    ) {

        checkUserExists(userId);

        Specification<UserFollow> filter = Specs.all(
                Specs.equal("follower.id", userId),
                Specs.contains("followed.username", username)
        );

        return PageResponse.from(
                userFollowRepository.findAll(filter, pageable),
                follow -> UserFollowResponse.of(
                        follow.getFollowed(),
                        follow.getCreatedAt()
                )
        );
    }

    public PageResponse<UserFollowResponse> getFollowers(
            Long userId,
            String username,
            Pageable pageable
    ) {

        checkUserExists(userId);

        Specification<UserFollow> filter = Specs.all(
                Specs.equal("followed.id", userId),
                Specs.contains("follower.username", username)
        );

        return PageResponse.from(
                userFollowRepository.findAll(filter, pageable),
                follow -> UserFollowResponse.of(
                        follow.getFollower(),
                        follow.getCreatedAt()
                )
        );
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

    /**
     * Activity of followed users, newest first. The feed merges three tables,
     * so filtering and paging happen in memory after the merge.
     */
    public PageResponse<ActivityResponse> getFeed(
            Long userId,
            String type,
            String username,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {

        checkUserExists(userId);

        List<Long> followedIds = userFollowRepository.findByFollowerId(userId)
                .stream()
                .map(follow -> follow.getFollowed().getId())
                .toList();

        List<ActivityResponse> activities = new ArrayList<>();

        if (!followedIds.isEmpty()) {

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
        }

        List<ActivityResponse> filtered = activities.stream()
                .filter(a -> type == null || type.isBlank()
                        || a.getType().equalsIgnoreCase(type))
                .filter(a -> username == null || username.isBlank()
                        || a.getUsername().toLowerCase().contains(username.toLowerCase()))
                .filter(a -> from == null
                        || !a.getTimestamp().isBefore(from.atStartOfDay()))
                .filter(a -> to == null
                        || a.getTimestamp().isBefore(to.plusDays(1).atStartOfDay()))
                .sorted(Comparator.comparing(ActivityResponse::getTimestamp)
                        .reversed())
                .toList();

        int start = (int) Math.min(pageable.getOffset(), filtered.size());
        int end = Math.min(start + pageable.getPageSize(), filtered.size());

        return PageResponse.from(
                new PageImpl<>(
                        filtered.subList(start, end),
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                        filtered.size()
                ),
                Function.identity()
        );
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
