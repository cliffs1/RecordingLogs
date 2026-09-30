package org.example.backendrl.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SongRatingService {

    private final SongRatingRepository songRatingRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public SongRatingService(
            SongRatingRepository songRatingRepository,
            SongRepository songRepository,
            UserRepository userRepository) {

        this.songRatingRepository = songRatingRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    public List<SongRatingResponse> getRatingsForSong(Long songId) {

        if (!songRepository.existsById(songId)) {
            throw new ResourceNotFoundException(
                    "Song with id " + songId + " not found"
            );
        }

        return songRatingRepository.findBySongId(songId)
                .stream()
                .map(SongRatingResponse::fromEntity)
                .toList();
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

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + request.getUserId()
                                        + " not found"
                        ));

        if (songRatingRepository.existsBySongIdAndUserId(
                songId,
                request.getUserId())) {

            throw new ConflictException(
                    "User " + request.getUserId()
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

        SongRating songRating = songRatingRepository
                .findBySongIdAndUserId(songId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User " + userId +
                                        " has not rated song " + songId
                        ));

        songRatingRepository.delete(songRating);
    }

    public List<SongRatingResponse> getRatingsForUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User with id " + userId + " not found"
            );
        }

        return songRatingRepository.findByUserId(userId)
                .stream()
                .map(SongRatingResponse::fromEntity)
                .toList();
    }
}
