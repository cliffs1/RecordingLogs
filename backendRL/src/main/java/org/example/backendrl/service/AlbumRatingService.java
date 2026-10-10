package org.example.backendrl.service;

import org.example.backendrl.dto.AlbumRatingRequest;
import org.example.backendrl.dto.AlbumRatingResponse;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.entity.Album;
import org.example.backendrl.entity.AlbumRating;
import org.example.backendrl.entity.User;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.AlbumRatingRepository;
import org.example.backendrl.repository.AlbumRepository;
import org.example.backendrl.repository.UserRepository;
import org.example.backendrl.repository.spec.Specs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AlbumRatingService {

    private final AlbumRatingRepository albumRatingRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    public AlbumRatingService(
            AlbumRatingRepository albumRatingRepository,
            AlbumRepository albumRepository,
            UserRepository userRepository) {

        this.albumRatingRepository = albumRatingRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
    }

    public PageResponse<AlbumRatingResponse> getRatingsForAlbum(
            Long albumId,
            Integer minRating,
            Integer maxRating,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {
        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException(
                    "Album with id " + albumId + " not found"
            );
        }

        return searchRatings(null, albumId, minRating, maxRating, from, to, pageable);
    }

    public AlbumRatingResponse getRating(
            Long albumId,
            Long userId) {

        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException(
                    "Album with id " + albumId + " not found"
            );
        }

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User with id " + userId + " not found"
            );
        }

        AlbumRating rating = albumRatingRepository
                .findByAlbumIdAndUserId(albumId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User " + userId
                                        + " has not rated album " + albumId
                        ));

        return AlbumRatingResponse.fromEntity(rating);
    }

    @Transactional
    public AlbumRatingResponse createRating(
            Long albumId,
            AlbumRatingRequest request) {

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

        if (albumRatingRepository.existsByAlbumIdAndUserId(
                albumId,
                request.getUserId())) {

            throw new ConflictException(
                    "User " + request.getUserId()
                            + " has already rated album " + albumId
            );
        }

        AlbumRating albumRating = new AlbumRating();

        albumRating.setAlbum(album);
        albumRating.setUser(user);
        albumRating.setRating(request.getRating());

        AlbumRating savedRating =
                albumRatingRepository.save(albumRating);

        return AlbumRatingResponse.fromEntity(savedRating);
    }

    @Transactional
    public AlbumRatingResponse updateRating(
            Long albumId,
            Long userId,
            AlbumRatingRequest request) {

        AlbumRating albumRating = albumRatingRepository
                .findByAlbumIdAndUserId(albumId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User " + userId
                                        + " has not rated album " + albumId
                        ));

        albumRating.setRating(request.getRating());

        AlbumRating updatedRating =
                albumRatingRepository.save(albumRating);

        return AlbumRatingResponse.fromEntity(updatedRating);
    }

    @Transactional
    public void deleteRating(
            Long albumId,
            Long userId) {

        AlbumRating albumRating = albumRatingRepository
                .findByAlbumIdAndUserId(albumId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User " + userId
                                        + " has not rated album " + albumId
                        ));

        albumRatingRepository.delete(albumRating);
    }

    public PageResponse<AlbumRatingResponse> getRatingsForUser(
            Long userId,
            Long albumId,
            Integer minRating,
            Integer maxRating,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User with id " + userId + " not found"
            );
        }

        return searchRatings(userId, albumId, minRating, maxRating, from, to, pageable);
    }

    private PageResponse<AlbumRatingResponse> searchRatings(
            Long userId,
            Long albumId,
            Integer minRating,
            Integer maxRating,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {
        Specification<AlbumRating> filter = Specs.all(
                Specs.equal("user.id", userId),
                Specs.equal("album.id", albumId),
                Specs.atLeast("rating", minRating),
                Specs.atMost("rating", maxRating),
                Specs.onOrAfter("updatedAt", from),
                Specs.onOrBefore("updatedAt", to)
        );

        return PageResponse.from(
                albumRatingRepository.findAll(filter, pageable),
                AlbumRatingResponse::fromEntity
        );
    }
}