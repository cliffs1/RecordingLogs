package org.example.backendrl.service;

import org.example.backendrl.dto.AlbumSummaryResponse;
import org.example.backendrl.dto.ArtistSummaryResponse;
import org.example.backendrl.entity.Album;
import org.example.backendrl.entity.Artist;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.AlbumRepository;
import org.example.backendrl.repository.ArtistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlbumArtistService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    public AlbumArtistService(
            AlbumRepository albumRepository,
            ArtistRepository artistRepository
    ) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }

    public List<ArtistSummaryResponse> getArtistsForAlbum(Long albumId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Album with id " + albumId + " not found"
                ));

        return album.getArtists()
                .stream()
                .map(ArtistSummaryResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void addArtistToAlbum(Long albumId, Long artistId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Album with id " + albumId + " not found"
                ));

        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artist with id " + artistId + " not found"
                ));

        if (album.getArtists().contains(artist)) {
            return;
        }

        album.getArtists().add(artist);
        albumRepository.save(album);
    }

    @Transactional
    public void removeArtistFromAlbum(Long albumId, Long artistId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Album with id " + albumId + " not found"
                ));

        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artist with id " + artistId + " not found"
                ));

        if (!album.getArtists().contains(artist)) {
            throw new ResourceNotFoundException(
                    "Artist " + artistId +
                            " is not associated with album " + albumId
            );
        }

        album.getArtists().remove(artist);
        albumRepository.save(album);
    }

    public List<AlbumSummaryResponse> getAlbumsForArtist(Long artistId) {

        if (!artistRepository.existsById(artistId)) {
            throw new ResourceNotFoundException(
                    "Artist with id " + artistId + " not found"
            );
        }

        List<Album> albums = albumRepository.findAll()
                .stream()
                .filter(album -> album.getArtists()
                        .stream()
                        .anyMatch(artist -> artist.getId().equals(artistId)))
                .toList();

        return albums.stream()
                .map(AlbumSummaryResponse::fromEntity)
                .toList();
    }
}
