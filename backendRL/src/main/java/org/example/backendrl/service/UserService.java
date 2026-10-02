package org.example.backendrl.service;

import org.example.backendrl.dto.UserRequest;
import org.example.backendrl.dto.UserResponse;
import org.example.backendrl.entity.User;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.AlbumFavoriteRepository;
import org.example.backendrl.repository.AlbumListRepository;
import org.example.backendrl.repository.AlbumRatingRepository;
import org.example.backendrl.repository.AlbumReviewRepository;
import org.example.backendrl.repository.SongRatingRepository;
import org.example.backendrl.repository.UserFollowRepository;
import org.example.backendrl.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AlbumRatingRepository albumRatingRepository;
    private final SongRatingRepository songRatingRepository;
    private final AlbumReviewRepository albumReviewRepository;
    private final AlbumFavoriteRepository albumFavoriteRepository;
    private final AlbumListRepository albumListRepository;
    private final UserFollowRepository userFollowRepository;

    public UserService(
            UserRepository userRepository,
            AlbumRatingRepository albumRatingRepository,
            SongRatingRepository songRatingRepository,
            AlbumReviewRepository albumReviewRepository,
            AlbumFavoriteRepository albumFavoriteRepository,
            AlbumListRepository albumListRepository,
            UserFollowRepository userFollowRepository) {

        this.userRepository = userRepository;
        this.albumRatingRepository = albumRatingRepository;
        this.songRatingRepository = songRatingRepository;
        this.albumReviewRepository = albumReviewRepository;
        this.albumFavoriteRepository = albumFavoriteRepository;
        this.albumListRepository = albumListRepository;
        this.userFollowRepository = userFollowRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + id + " not found"
                        ));

        return UserResponse.fromEntity(user);
    }

    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException(
                    "Username '" + request.getUsername() + "' already exists"
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(
                    "Email '" + request.getEmail() + "' already exists"
            );
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setDisplayName(request.getDisplayName());

        User savedUser = userRepository.save(user);

        return UserResponse.fromEntity(savedUser);
    }

    public UserResponse updateUser(Long id, UserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + id + " not found"
                        ));

        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {

            throw new ConflictException(
                    "Username '" + request.getUsername() + "' already exists"
            );
        }

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            throw new ConflictException(
                    "Email '" + request.getEmail() + "' already exists"
            );
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setDisplayName(request.getDisplayName());

        User updatedUser = userRepository.save(user);

        return UserResponse.fromEntity(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "User with id " + id + " not found"
            );
        }

        albumRatingRepository.deleteByUserId(id);
        songRatingRepository.deleteByUserId(id);
        albumReviewRepository.deleteByUserId(id);
        albumFavoriteRepository.deleteByUserId(id);
        albumListRepository.deleteAll(albumListRepository.findByUserId(id));
        userFollowRepository.deleteByFollowerId(id);
        userFollowRepository.deleteByFollowedId(id);

        userRepository.deleteById(id);
    }
}
