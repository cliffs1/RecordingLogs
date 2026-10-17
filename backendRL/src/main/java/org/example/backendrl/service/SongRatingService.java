package org.example.backendrl.service;

import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.dto.SongRatingRequest;
import org.example.backendrl.dto.SongRatingResponse;
import org.example.backendrl.entity.Song;
import org.example.backendrl.entity.SongRating;
import org.example.backendrl.entity.User;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.SongRatingRepository;
import org.example.backendrl.repository.SongRepository;
import org.example.backendrl.repository.UserRepository;
import org.example.backendrl.repository.spec.Specs;
import org.example.backendrl.security.CurrentUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class SongRatingService {

    private final SongRatingRepository songRatingRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public SongRatingService(
            SongRatingRepository songRatingRepository,
            SongRepository songRepository,
            UserRepository userRepository,
            CurrentUser currentUser) {

        this.songRatingRepository = songRatingRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    public PageResponse<SongRatingResponse> getRatingsForSong(
            Long songId,
            Integer minRating,
            Integer maxRating,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {
        if (!songRepository.existsById(songId)) {
            throw new ResourceNotFoundException(
                    "Song with id " + songId + " not found"
            );
        }

        return searchRatings(null, songId, minRating, maxRating, from, to, pageable);
    }

    public SongRatingResponse getRating(Long songId, Long userId) {

        if (!songRepository.existsById(songId)) {
            throw new ResourceNotFoundException(
                    "Song with id " + songId + " not found"
            );
        }

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User with id " + userId + " not found"
            );
        }

        SongRating rating = songRatingRepository
                .findBySongIdAndUserId(songId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User " + userId +
                                        " has not rated song " + songId
                        ));

        return SongRatingResponse.fromEntity(rating);
    }

    @Transactional
    public SongRatingResponse createRating(
            Long songId,
            SongRatingRequest request) {

        Song song = songRepository.findById(songId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Song with id " + songId + " not found"
                        ));

        Long userId = currentUser.id();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + userId + " not found"
                        ));

        if (songRatingRepository.existsBySongIdAndUserId(
                songId,
                userId)) {

            throw new ConflictException(
                    "User " + userId
                            + " has already rated song " + songId
            );
        }

        SongRating songRating = new SongRating();

        songRating.setSong(song);
        songRating.setUser(user);
        songRating.setRating(request.getRating());

        SongRating savedRating =
                songRatingRepository.save(songRating);

        return SongRatingResponse.fromEntity(savedRating);
    }

    @Transactional
    public SongRatingResponse updateRating(
            Long songId,
            Long userId,
            SongRatingRequest request) {

        currentUser.requireSelf(userId);

        SongRating songRating = songRatingRepository
                .findBySongIdAndUserId(songId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User " + userId +
                                        " has not rated song " + songId
                        ));

        songRating.setRating(request.getRating());

        SongRating updatedRating =
                songRatingRepository.save(songRating);

        return SongRatingResponse.fromEntity(updatedRating);
    }

    @Transactional
    public void deleteRating(Long songId, Long userId) {

        currentUser.requireSelfOrModerator(userId);

        SongRating songRating = songRatingRepository
                .findBySongIdAndUserId(songId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User " + userId +
                                        " has not rated song " + songId
                        ));

        songRatingRepository.delete(songRating);
    }

    public PageResponse<SongRatingResponse> getRatingsForUser(
            Long userId,
            Long songId,
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

        return searchRatings(userId, songId, minRating, maxRating, from, to, pageable);
    }

    private PageResponse<SongRatingResponse> searchRatings(
            Long userId,
            Long songId,
            Integer minRating,
            Integer maxRating,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {
        Specification<SongRating> filter = Specs.all(
                Specs.equal("user.id", userId),
                Specs.equal("song.id", songId),
                Specs.atLeast("rating", minRating),
                Specs.atMost("rating", maxRating),
                Specs.onOrAfter("updatedAt", from),
                Specs.onOrBefore("updatedAt", to)
        );

        return PageResponse.from(
                songRatingRepository.findAll(filter, pageable),
                SongRatingResponse::fromEntity
        );
    }
}
