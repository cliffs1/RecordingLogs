package org.example.backendrl.service;

import org.example.backendrl.dto.AlbumFavoriteResponse;
import org.example.backendrl.entity.Album;
import org.example.backendrl.entity.AlbumFavorite;
import org.example.backendrl.entity.User;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.AlbumFavoriteRepository;
import org.example.backendrl.repository.AlbumRepository;
import org.example.backendrl.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlbumFavoriteService {

    private final AlbumFavoriteRepository albumFavoriteRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    public AlbumFavoriteService(
            AlbumFavoriteRepository albumFavoriteRepository,
            AlbumRepository albumRepository,
            UserRepository userRepository) {

        this.albumFavoriteRepository = albumFavoriteRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
    }

    public List<AlbumFavoriteResponse> getFavoritesForUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User with id " + userId + " not found"
            );
        }

        return albumFavoriteRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(AlbumFavoriteResponse::fromEntity)
                .toList();
    }

    @Transactional
    public AlbumFavoriteResponse addFavorite(
            Long userId,
            Long albumId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + userId + " not found"
                        ));

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Album with id " + albumId + " not found"
                        ));

        if (albumFavoriteRepository.existsByUserIdAndAlbumId(userId, albumId)) {
            throw new ConflictException(
                    "Album " + albumId
                            + " is already a favorite of user " + userId
            );
        }

        AlbumFavorite favorite = new AlbumFavorite();
        favorite.setUser(user);
        favorite.setAlbum(album);

        AlbumFavorite savedFavorite = albumFavoriteRepository.save(favorite);

        return AlbumFavoriteResponse.fromEntity(savedFavorite);
    }

    @Transactional
    public void removeFavorite(
            Long userId,
            Long albumId) {

        AlbumFavorite favorite = albumFavoriteRepository
                .findByUserIdAndAlbumId(userId, albumId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Album " + albumId
                                        + " is not a favorite of user " + userId
                        ));

        albumFavoriteRepository.delete(favorite);
    }
}
