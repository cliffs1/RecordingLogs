package org.example.backendrl.service;

import org.example.backendrl.dto.ArtistRequest;
import org.example.backendrl.dto.ArtistResponse;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.entity.Album;
import org.example.backendrl.entity.Artist;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.ArtistGenreRepository;
import org.example.backendrl.repository.ArtistRepository;
import org.example.backendrl.repository.spec.ArtistSpecs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistGenreRepository artistGenreRepository;
    private final AlbumService albumService;

    public ArtistService(
            ArtistRepository artistRepository,
            ArtistGenreRepository artistGenreRepository,
            AlbumService albumService) {

        this.artistRepository = artistRepository;
        this.artistGenreRepository = artistGenreRepository;
        this.albumService = albumService;
    }

    public PageResponse<ArtistResponse> getArtists(
            String name,
            String genre,
            Pageable pageable
    ) {
        List<Specification<Artist>> filters = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            filters.add(ArtistSpecs.nameContains(name));
        }

        if (genre != null && !genre.isBlank()) {
            filters.add(ArtistSpecs.hasGenre(genre));
        }

        return PageResponse.from(
                artistRepository.findAll(Specification.allOf(filters), pageable),
                this::toResponse
        );
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

    @Transactional
    public void deleteArtist(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artist with id " + id + " not found"
                ));

        artistGenreRepository.deleteByArtistId(id);

        for (Album album : artist.getAlbums()) {
            albumService.deleteAlbumDependents(album);
        }

        // albums and their songs are removed by cascade
        artistRepository.delete(artist);
    }

    private ArtistResponse toResponse(Artist artist) {
        ArtistResponse response = new ArtistResponse(
                artist.getId(),
                artist.getName(),
                artist.getBiography()
        );

        String self = "/api/artists/" + artist.getId();

        response.addLink("self", self);
        response.addLink("albums", self + "/albums");
        response.addLink("genres", self + "/genres");

        return response;
    }
}
