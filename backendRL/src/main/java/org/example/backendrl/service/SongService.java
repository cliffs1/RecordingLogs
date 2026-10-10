package org.example.backendrl.service;

import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.dto.SongRequest;
import org.example.backendrl.dto.SongResponse;
import org.example.backendrl.entity.Album;
import org.example.backendrl.entity.Song;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.SongRatingRepository;
import org.example.backendrl.repository.SongRepository;
import org.example.backendrl.repository.spec.SongSpecs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final SongRatingRepository songRatingRepository;
    private final AlbumService albumService;

    public SongService(
            SongRepository songRepository,
            SongRatingRepository songRatingRepository,
            AlbumService albumService) {

        this.songRepository = songRepository;
        this.songRatingRepository = songRatingRepository;
        this.albumService = albumService;
    }

    public PageResponse<SongResponse> getSongs(
            Long artistId,
            Long albumId,
            String title,
            Integer minDuration,
            Integer maxDuration,
            Integer disc,
            Pageable pageable
    ) {
        Album album = albumService.findAlbum(artistId, albumId);

        List<Specification<Song>> filters = new ArrayList<>();
        filters.add(SongSpecs.byAlbum(album.getId()));

        if (title != null && !title.isBlank()) {
            filters.add(SongSpecs.titleContains(title));
        }

        if (minDuration != null) {
            filters.add(SongSpecs.minDuration(minDuration));
        }

        if (maxDuration != null) {
            filters.add(SongSpecs.maxDuration(maxDuration));
        }

        if (disc != null) {
            filters.add(SongSpecs.onDisc(disc));
        }

        return PageResponse.from(
                songRepository.findAll(Specification.allOf(filters), pageable),
                this::toResponse
        );
    }

    public SongResponse getSong(Long artistId, Long albumId, Long id) {
        return toResponse(findSong(artistId, albumId, id));
    }

    @Transactional
    public SongResponse createSong(Long artistId, Long albumId, SongRequest request) {
        Album album = albumService.findAlbum(artistId, albumId);

        Song song = new Song();
        song.setAlbum(album);
        applyRequest(song, request);

        Song savedSong = songRepository.save(song);

        return toResponse(savedSong);
    }

    @Transactional
    public SongResponse updateSong(Long artistId, Long albumId, Long id, SongRequest request) {
        Song song = findSong(artistId, albumId, id);

        applyRequest(song, request);

        Song updatedSong = songRepository.save(song);

        return toResponse(updatedSong);
    }

    @Transactional
    public void deleteSong(Long artistId, Long albumId, Long id) {
        Song song = findSong(artistId, albumId, id);

        songRatingRepository.deleteBySongId(song.getId());

        song.getAlbum().getSongs().remove(song);
        songRepository.delete(song);
    }

    private Song findSong(Long artistId, Long albumId, Long id) {
        Album album = albumService.findAlbum(artistId, albumId);

        return songRepository.findByIdAndAlbumId(id, album.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song with id " + id + " not found in album " + albumId
                ));
    }

    private void applyRequest(Song song, SongRequest request) {
        song.setTitle(request.getTitle());
        song.setDurationSeconds(request.getDurationSeconds());
        song.setOriginalReleaseDate(request.getOriginalReleaseDate());
        song.setTrackNumber(request.getTrackNumber());
        song.setDiscNumber(request.getDiscNumber());
    }

    private SongResponse toResponse(Song song) {
        SongResponse response = new SongResponse(
                song.getId(),
                song.getAlbum().getId(),
                song.getTitle(),
                song.getDurationSeconds(),
                song.getOriginalReleaseDate(),
                song.getTrackNumber(),
                song.getDiscNumber()
        );

        String artist = "/api/artists/" + song.getAlbum().getArtist().getId();
        String album = artist + "/albums/" + song.getAlbum().getId();

        response.addLink("self", album + "/songs/" + song.getId());
        response.addLink("album", album);
        response.addLink("artist", artist);
        response.addLink("ratings", "/api/songs/" + song.getId() + "/ratings");
        response.addLink("genres", "/api/songs/" + song.getId() + "/genres");

        return response;
    }
}
