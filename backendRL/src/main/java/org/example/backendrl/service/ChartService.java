package org.example.backendrl.service;

import org.example.backendrl.dto.PageResponse;
import org.example.backendrl.dto.TopAlbumResponse;
import org.example.backendrl.dto.TopSongResponse;
import org.example.backendrl.entity.Album;
import org.example.backendrl.entity.Song;
import org.example.backendrl.repository.AlbumRatingRepository;
import org.example.backendrl.repository.AlbumRepository;
import org.example.backendrl.repository.RatingStats;
import org.example.backendrl.repository.SongRatingRepository;
import org.example.backendrl.repository.SongRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Top-rated charts: combine albums/songs, their artists and user ratings
 * into one ranked list.
 */
@Service
@Transactional(readOnly = true)
public class ChartService {

    private final AlbumRatingRepository albumRatingRepository;
    private final SongRatingRepository songRatingRepository;
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;

    public ChartService(
            AlbumRatingRepository albumRatingRepository,
            SongRatingRepository songRatingRepository,
            AlbumRepository albumRepository,
            SongRepository songRepository) {

        this.albumRatingRepository = albumRatingRepository;
        this.songRatingRepository = songRatingRepository;
        this.albumRepository = albumRepository;
        this.songRepository = songRepository;
    }

    public PageResponse<TopAlbumResponse> getTopAlbums(
            String genre,
            Integer year,
            Long artistId,
            int minRatings,
            Pageable pageable
    ) {
        // the chart has its own fixed order, so any requested sort is ignored
        Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Page<RatingStats> stats = albumRatingRepository.findTopAlbums(
                genreFilter(genre), year, artistId, Math.max(minRatings, 1), page
        );

        Map<Long, Album> albums = albumRepository.findAllById(ids(stats))
                .stream()
                .collect(Collectors.toMap(Album::getId, Function.identity()));

        List<TopAlbumResponse> entries = new ArrayList<>();
        int rank = (int) page.getOffset();

        for (RatingStats stat : stats) {
            entries.add(toAlbumEntry(++rank, stat, albums.get(stat.getId())));
        }

        return PageResponse.of(stats, entries);
    }

    public PageResponse<TopSongResponse> getTopSongs(
            String genre,
            Integer year,
            Long artistId,
            int minRatings,
            Pageable pageable
    ) {
        Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Page<RatingStats> stats = songRatingRepository.findTopSongs(
                genreFilter(genre), year, artistId, Math.max(minRatings, 1), page
        );

        Map<Long, Song> songs = songRepository.findAllById(ids(stats))
                .stream()
                .collect(Collectors.toMap(Song::getId, Function.identity()));

        List<TopSongResponse> entries = new ArrayList<>();
        int rank = (int) page.getOffset();

        for (RatingStats stat : stats) {
            entries.add(toSongEntry(++rank, stat, songs.get(stat.getId())));
        }

        return PageResponse.of(stats, entries);
    }

    private TopAlbumResponse toAlbumEntry(int rank, RatingStats stat, Album album) {
        TopAlbumResponse entry = new TopAlbumResponse(
                rank,
                album.getId(),
                album.getTitle(),
                album.getReleaseDate(),
                album.getCoverUrl(),
                album.getArtist().getId(),
                album.getArtist().getName(),
                round(stat.getAverage()),
                stat.getRatingCount()
        );

        String artist = "/api/artists/" + album.getArtist().getId();

        entry.addLink("album", artist + "/albums/" + album.getId());
        entry.addLink("artist", artist);
        entry.addLink("ratings", "/api/albums/" + album.getId() + "/ratings");
        entry.addExternalLink("cover", album.getCoverUrl());

        return entry;
    }

    private TopSongResponse toSongEntry(int rank, RatingStats stat, Song song) {
        Album album = song.getAlbum();

        TopSongResponse entry = new TopSongResponse(
                rank,
                song.getId(),
                song.getTitle(),
                song.getDurationSeconds(),
                album.getId(),
                album.getTitle(),
                album.getCoverUrl(),
                album.getArtist().getId(),
                album.getArtist().getName(),
                round(stat.getAverage()),
                stat.getRatingCount()
        );

        String artist = "/api/artists/" + album.getArtist().getId();
        String albumPath = artist + "/albums/" + album.getId();

        entry.addLink("song", albumPath + "/songs/" + song.getId());
        entry.addLink("album", albumPath);
        entry.addLink("artist", artist);
        entry.addLink("ratings", "/api/songs/" + song.getId() + "/ratings");
        entry.addExternalLink("cover", album.getCoverUrl());

        return entry;
    }

    private static List<Long> ids(Page<RatingStats> stats) {
        return stats.getContent().stream().map(RatingStats::getId).toList();
    }

    private static double round(Double value) {
        return Math.round(value * 100) / 100.0;
    }

    /**
     * Genre filter value for the chart queries: lower-cased, or null to skip it.
     */
    private static String genreFilter(String value) {
        return value == null || value.isBlank() ? null : value.toLowerCase();
    }
}
