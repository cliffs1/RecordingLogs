package org.example.backendrl.service;

import org.example.backendrl.dto.AlbumReviewRequest;
import org.example.backendrl.dto.AlbumReviewResponse;
import org.example.backendrl.entity.Album;
import org.example.backendrl.entity.AlbumReview;
import org.example.backendrl.entity.User;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.exception.UnprocessableEntityException;
import org.example.backendrl.repository.AlbumRepository;
import org.example.backendrl.repository.AlbumReviewRepository;
import org.example.backendrl.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlbumReviewService {

    private final AlbumReviewRepository albumReviewRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    public AlbumReviewService(
            AlbumReviewRepository albumReviewRepository,
            AlbumRepository albumRepository,
            UserRepository userRepository) {

        this.albumReviewRepository = albumReviewRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
    }

    public List<AlbumReviewResponse> getAllReviews() {

        return albumReviewRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(AlbumReviewResponse::fromEntity)
                .toList();
    }

    public List<AlbumReviewResponse> getReviewsForAlbum(Long albumId) {

        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException(
                    "Album with id " + albumId + " not found"
            );
        }

        return albumReviewRepository.findByAlbumIdOrderByCreatedAtDesc(albumId)
                .stream()
                .map(AlbumReviewResponse::fromEntity)
                .toList();
    }

    public List<AlbumReviewResponse> getReviewsForUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User with id " + userId + " not found"
            );
        }

        return albumReviewRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(AlbumReviewResponse::fromEntity)
                .toList();
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

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + request.getUserId()
                                        + " not found"
                        ));

        if (albumReviewRepository.existsByAlbumIdAndUserId(
                albumId,
                request.getUserId())) {

            throw new ConflictException(
                    "User " + request.getUserId()
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

        if (!review.getUser().getId().equals(request.getUserId())) {
            throw new UnprocessableEntityException(
                    "User " + request.getUserId()
                            + " is not the author of review " + reviewId
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
