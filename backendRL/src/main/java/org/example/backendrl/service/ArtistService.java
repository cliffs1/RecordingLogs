package org.example.backendrl.service;

import org.example.backendrl.dto.ArtistRequest;
import org.example.backendrl.dto.ArtistResponse;
import org.example.backendrl.entity.Artist;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.ArtistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<ArtistResponse> getAllArtists() {
        return artistRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ArtistResponse getArtist(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artist with id " + id + " not found"
                ));

        return toResponse(artist);
    }

    public ArtistResponse createArtist(ArtistRequest request) {
        Artist artist = new Artist();

        artist.setName(request.getName());
        artist.setBiography(request.getBiography());

        Artist savedArtist = artistRepository.save(artist);

        return toResponse(savedArtist);
    }

    public ArtistResponse updateArtist(Long id, ArtistRequest request) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artist with id " + id + " not found"
                ));

        artist.setName(request.getName());
        artist.setBiography(request.getBiography());

        Artist updatedArtist = artistRepository.save(artist);

        return toResponse(updatedArtist);
    }

    public void deleteArtist(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Artist with id " + id + " not found"
            );
        }

        artistRepository.deleteById(id);
    }

    private ArtistResponse toResponse(Artist artist) {
        return new ArtistResponse(
                artist.getId(),
                artist.getName(),
                artist.getBiography()
        );
    }
}