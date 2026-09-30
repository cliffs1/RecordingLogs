package org.example.backendrl.service;

import org.example.backendrl.dto.AlbumRatingRequest;
import org.example.backendrl.dto.AlbumRatingResponse;
import org.example.backendrl.entity.Album;
import org.example.backendrl.entity.AlbumRating;
import org.example.backendrl.entity.User;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.AlbumRatingRepository;
import org.example.backendrl.repository.AlbumRepository;
import org.example.backendrl.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<AlbumRatingResponse> getRatingsForAlbum(Long albumId) {

        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException(
                    "Album with id " + albumId + " not found"
            );
        }

        return albumRatingRepository.findByAlbumId(albumId)
                .stream()
                .map(AlbumRatingResponse::fromEntity)
                .toList();
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

    public List<AlbumRatingResponse> getRatingsForUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User with id " + userId + " not found"
            );
        }

        return albumRatingRepository.findByUserId(userId)
                .stream()
                .map(AlbumRatingResponse::fromEntity)
                .toList();
    }
}