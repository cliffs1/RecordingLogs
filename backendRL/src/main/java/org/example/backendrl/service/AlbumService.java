package org.example.backendrl.service;

import org.example.backendrl.dto.AlbumRequest;
import org.example.backendrl.dto.AlbumResponse;
import org.example.backendrl.entity.Album;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.AlbumRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public List<AlbumResponse> getAllAlbums() {

        return albumRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AlbumResponse getAlbum(Long id) {

        Album album = albumRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Album with id " + id + " not found"
                        )
                );

        return toResponse(album);
    }

    public AlbumResponse createAlbum(AlbumRequest request) {

        Album album = new Album();

        album.setTitle(request.getTitle());
        album.setReleaseDate(request.getReleaseDate());
        album.setCoverUrl(request.getCoverUrl());

        Album savedAlbum = albumRepository.save(album);

        return toResponse(savedAlbum);
    }

    public AlbumResponse updateAlbum(
            Long id,
            AlbumRequest request
    ) {

        Album album = albumRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Album with id " + id + " not found"
                        )
                );

        album.setTitle(request.getTitle());
        album.setReleaseDate(request.getReleaseDate());
        album.setCoverUrl(request.getCoverUrl());

        Album updatedAlbum = albumRepository.save(album);

        return toResponse(updatedAlbum);
    }

    public void deleteAlbum(Long id) {

        if (!albumRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Album with id " + id + " not found"
            );
        }

        albumRepository.deleteById(id);
    }

    private AlbumResponse toResponse(Album album) {

        return new AlbumResponse(
                album.getId(),
                album.getTitle(),
                album.getReleaseDate(),
                album.getCoverUrl()
        );
    }
}
