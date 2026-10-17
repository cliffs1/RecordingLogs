package org.example.backendrl.service;

import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.dto.UserRequest;
import org.example.backendrl.dto.UserResponse;
import org.example.backendrl.dto.UserUpdateRequest;
import org.example.backendrl.entity.Role;
import org.example.backendrl.entity.User;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ForbiddenException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.AlbumFavoriteRepository;
import org.example.backendrl.repository.AlbumListRepository;
import org.example.backendrl.repository.AlbumRatingRepository;
import org.example.backendrl.repository.AlbumReviewRepository;
import org.example.backendrl.repository.RefreshTokenRepository;
import org.example.backendrl.repository.SongRatingRepository;
import org.example.backendrl.repository.UserFollowRepository;
import org.example.backendrl.repository.UserRepository;
import org.example.backendrl.repository.spec.Specs;
import org.example.backendrl.security.CurrentUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AlbumRatingRepository albumRatingRepository;
    private final SongRatingRepository songRatingRepository;
    private final AlbumReviewRepository albumReviewRepository;
    private final AlbumFavoriteRepository albumFavoriteRepository;
    private final AlbumListRepository albumListRepository;
    private final UserFollowRepository userFollowRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;

    public UserService(
            UserRepository userRepository,
            AlbumRatingRepository albumRatingRepository,
            SongRatingRepository songRatingRepository,
            AlbumReviewRepository albumReviewRepository,
            AlbumFavoriteRepository albumFavoriteRepository,
            AlbumListRepository albumListRepository,
            UserFollowRepository userFollowRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            CurrentUser currentUser) {

        this.userRepository = userRepository;
        this.albumRatingRepository = albumRatingRepository;
        this.songRatingRepository = songRatingRepository;
        this.albumReviewRepository = albumReviewRepository;
        this.albumFavoriteRepository = albumFavoriteRepository;
        this.albumListRepository = albumListRepository;
        this.userFollowRepository = userFollowRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUser = currentUser;
    }

    public PageResponse<UserResponse> getAllUsers(
            String username,
            String displayName,
            Pageable pageable
    ) {
        Specification<User> filter = Specs.all(
                Specs.contains("username", username),
                Specs.contains("displayName", displayName)
        );

        return PageResponse.from(
                userRepository.findAll(filter, pageable),
                UserResponse::fromEntity
        );
    }

    public UserResponse getUserById(Long id) {

        return UserResponse.fromEntity(findUser(id));
    }

    /**
     * Registers a new user with the USER role.
     */
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
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName());
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return UserResponse.fromEntity(savedUser);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {

        currentUser.requireSelfOrAdmin(id);

        User user = findUser(id);

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
        user.setDisplayName(request.getDisplayName());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            refreshTokenRepository.revokeAllByUserId(id);
        }

        User updatedUser = userRepository.save(user);

        return UserResponse.fromEntity(updatedUser);
    }

    /**
     * Changes the role of a user (administrators only). The user's refresh tokens are revoked,
     * so the new role is applied the next time they log in.
     */
    @Transactional
    public UserResponse updateRole(Long id, Role role) {

        if (currentUser.id().equals(id)) {
            throw new ForbiddenException("You cannot change your own role");
        }

        User user = findUser(id);
        user.setRole(role);

        refreshTokenRepository.revokeAllByUserId(id);

        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {

        currentUser.requireSelfOrAdmin(id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "User with id " + id + " not found"
            );
        }

        refreshTokenRepository.deleteByUserId(id);
        albumRatingRepository.deleteByUserId(id);
        songRatingRepository.deleteByUserId(id);
        albumReviewRepository.deleteByUserId(id);
        albumFavoriteRepository.deleteByUserId(id);
        albumListRepository.deleteAll(albumListRepository.findByUserId(id));
        userFollowRepository.deleteByFollowerId(id);
        userFollowRepository.deleteByFollowedId(id);

        userRepository.deleteById(id);
    }

    private User findUser(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + id + " not found"
                        ));
    }
}
