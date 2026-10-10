package org.example.backendrl.service;

import org.example.backendrl.dto.AlbumRequest;
import org.example.backendrl.dto.AlbumResponse;
import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.entity.Album;
import org.example.backendrl.entity.Artist;
import org.example.backendrl.entity.Song;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.AlbumFavoriteRepository;
import org.example.backendrl.repository.AlbumListItemRepository;
import org.example.backendrl.repository.AlbumRatingRepository;
import org.example.backendrl.repository.AlbumRepository;
import org.example.backendrl.repository.AlbumReviewRepository;
import org.example.backendrl.repository.ArtistRepository;
import org.example.backendrl.repository.SongRatingRepository;
import org.example.backendrl.repository.spec.AlbumSpecs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRatingRepository albumRatingRepository;
    private final AlbumReviewRepository albumReviewRepository;
    private final AlbumFavoriteRepository albumFavoriteRepository;
    private final AlbumListItemRepository albumListItemRepository;
    private final SongRatingRepository songRatingRepository;

    public AlbumService(
            AlbumRepository albumRepository,
            ArtistRepository artistRepository,
            AlbumRatingRepository albumRatingRepository,
            AlbumReviewRepository albumReviewRepository,
            AlbumFavoriteRepository albumFavoriteRepository,
            AlbumListItemRepository albumListItemRepository,
            SongRatingRepository songRatingRepository) {

        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.albumRatingRepository = albumRatingRepository;
        this.albumReviewRepository = albumReviewRepository;
        this.albumFavoriteRepository = albumFavoriteRepository;
        this.albumListItemRepository = albumListItemRepository;
        this.songRatingRepository = songRatingRepository;
    }

    public PageResponse<AlbumResponse> getAlbums(
            Long artistId,
            String title,
            LocalDate releasedFrom,
            LocalDate releasedTo,
            String genre,
            Pageable pageable
    ) {

        Artist artist = findArtist(artistId);

        List<Specification<Album>> filters = new ArrayList<>();
        filters.add(AlbumSpecs.byArtist(artist.getId()));

        if (title != null && !title.isBlank()) {
            filters.add(AlbumSpecs.titleContains(title));
        }

        if (releasedFrom != null) {
            filters.add(AlbumSpecs.releasedFrom(releasedFrom));
        }

        if (releasedTo != null) {
            filters.add(AlbumSpecs.releasedTo(releasedTo));
        }

        if (genre != null && !genre.isBlank()) {
            filters.add(AlbumSpecs.hasGenre(genre));
        }

        return PageResponse.from(
                albumRepository.findAll(Specification.allOf(filters), pageable),
                this::toResponse
        );
    }

    public AlbumResponse getAlbum(Long artistId, Long id) {

        return toResponse(findAlbum(artistId, id));
    }

    @Transactional
    public AlbumResponse createAlbum(Long artistId, AlbumRequest request) {

        Artist artist = findArtist(artistId);

        Album album = new Album();

        album.setArtist(artist);
        album.setTitle(request.getTitle());
        album.setReleaseDate(request.getReleaseDate());
        album.setCoverUrl(request.getCoverUrl());

        Album savedAlbum = albumRepository.save(album);

        return toResponse(savedAlbum);
    }

    @Transactional
    public AlbumResponse updateAlbum(
            Long artistId,
            Long id,
            AlbumRequest request
    ) {

        Album album = findAlbum(artistId, id);

        album.setTitle(request.getTitle());
        album.setReleaseDate(request.getReleaseDate());
        album.setCoverUrl(request.getCoverUrl());

        Album updatedAlbum = albumRepository.save(album);

        return toResponse(updatedAlbum);
    }

    @Transactional
    public void deleteAlbum(Long artistId, Long id) {

        Album album = findAlbum(artistId, id);

        deleteAlbumDependents(album);

        album.getArtist().getAlbums().remove(album);
        albumRepository.delete(album);
    }

    /**
     * Finds an album that belongs to the given artist.
     * Throws 404 if the artist does not exist or the album is not theirs.
     */
    public Album findAlbum(Long artistId, Long id) {

        Artist artist = findArtist(artistId);

        return albumRepository.findByIdAndArtistId(id, artist.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Album with id " + id + " not found for artist " + artistId
                        )
                );
    }

    /**
     * Removes user activity that points at the album or its songs,
     * so the album itself can be deleted (songs and genres go by cascade).
     */
    @Transactional
    public void deleteAlbumDependents(Album album) {

        Long id = album.getId();

        albumRatingRepository.deleteByAlbumId(id);
        albumReviewRepository.deleteByAlbumId(id);
        albumFavoriteRepository.deleteByAlbumId(id);
        albumListItemRepository.deleteByAlbumId(id);

        for (Song song : album.getSongs()) {
            songRatingRepository.deleteBySongId(song.getId());
        }
    }

    private Artist findArtist(Long artistId) {

        return artistRepository.findById(artistId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Artist with id " + artistId + " not found"
                        )
                );
    }

    private AlbumResponse toResponse(Album album) {

        AlbumResponse response = new AlbumResponse(
                album.getId(),
                album.getArtist().getId(),
                album.getTitle(),
                album.getReleaseDate(),
                album.getCoverUrl()
        );

        String artist = "/api/artists/" + album.getArtist().getId();
        String self = artist + "/albums/" + album.getId();

        response.addLink("self", self);
        response.addLink("artist", artist);
        response.addLink("songs", self + "/songs");
        response.addLink("ratings", "/api/albums/" + album.getId() + "/ratings");
        response.addLink("reviews", "/api/albums/" + album.getId() + "/reviews");
        response.addLink("genres", "/api/albums/" + album.getId() + "/genres");
        response.addExternalLink("cover", album.getCoverUrl());

        return response;
    }
}
