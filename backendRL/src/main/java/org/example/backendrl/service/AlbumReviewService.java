package org.example.backendrl.service;

import org.example.backendrl.dto.AlbumReviewRequest;
import org.example.backendrl.dto.AlbumReviewResponse;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.entity.Album;
import org.example.backendrl.entity.AlbumReview;
import org.example.backendrl.entity.User;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.exception.ForbiddenException;
import org.example.backendrl.repository.AlbumRepository;
import org.example.backendrl.repository.AlbumReviewRepository;
import org.example.backendrl.repository.UserRepository;
import org.example.backendrl.repository.spec.Specs;
import org.example.backendrl.security.CurrentUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AlbumReviewService {

    private final AlbumReviewRepository albumReviewRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public AlbumReviewService(
            AlbumReviewRepository albumReviewRepository,
            AlbumRepository albumRepository,
            UserRepository userRepository,
            CurrentUser currentUser) {

        this.albumReviewRepository = albumReviewRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    public PageResponse<AlbumReviewResponse> getAllReviews(
            String title,
            Long userId,
            Long albumId,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {
        return searchReviews(title, userId, albumId, from, to, pageable);
    }

    public PageResponse<AlbumReviewResponse> getReviewsForAlbum(
            Long albumId,
            String title,
            Long userId,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {
        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException(
                    "Album with id " + albumId + " not found"
            );
        }

        return searchReviews(title, userId, albumId, from, to, pageable);
    }

    public PageResponse<AlbumReviewResponse> getReviewsForUser(
            Long userId,
            String title,
            Long albumId,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User with id " + userId + " not found"
            );
        }

        return searchReviews(title, userId, albumId, from, to, pageable);
    }

    private PageResponse<AlbumReviewResponse> searchReviews(
            String title,
            Long userId,
            Long albumId,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {
        Specification<AlbumReview> filter = Specs.all(
                Specs.equal("user.id", userId),
                Specs.equal("album.id", albumId),
                Specs.contains("title", title),
                Specs.onOrAfter("createdAt", from),
                Specs.onOrBefore("createdAt", to)
        );

        return PageResponse.from(
                albumReviewRepository.findAll(filter, pageable),
                AlbumReviewResponse::fromEntity
        );
    }

    public AlbumReviewResponse getReview(Long reviewId) {

        return AlbumReviewResponse.fromEntity(findReview(reviewId));
    }

    @Transactional
    public AlbumReviewResponse createReview(
            Long albumId,
            AlbumReviewRequest request) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Album with id " + albumId + " not found"
                        ));

        Long userId = currentUser.id();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + userId + " not found"
                        ));

        if (albumReviewRepository.existsByAlbumIdAndUserId(
                albumId,
                userId)) {

            throw new ConflictException(
                    "User " + userId
                            + " has already reviewed album " + albumId
            );
        }

        AlbumReview review = new AlbumReview();
        review.setAlbum(album);
        review.setUser(user);
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());

        AlbumReview savedReview = albumReviewRepository.save(review);

        return AlbumReviewResponse.fromEntity(savedReview);
    }

    @Transactional
    public AlbumReviewResponse updateReview(
            Long reviewId,
            AlbumReviewRequest request) {

        AlbumReview review = findReview(reviewId);

        if (!review.getUser().getId().equals(currentUser.id())) {
            throw new ForbiddenException(
                    "You are not the author of review " + reviewId
            );
        }

        review.setTitle(request.getTitle());
        review.setContent(request.getContent());

        AlbumReview updatedReview = albumReviewRepository.save(review);

        return AlbumReviewResponse.fromEntity(updatedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId) {

        AlbumReview review = findReview(reviewId);

        currentUser.requireSelfOrModerator(review.getUser().getId());

        albumReviewRepository.delete(review);
    }

    private AlbumReview findReview(Long reviewId) {

        return albumReviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review with id " + reviewId + " not found"
                        ));
    }
}
