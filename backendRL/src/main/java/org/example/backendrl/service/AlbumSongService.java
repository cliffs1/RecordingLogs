package org.example.backendrl.service;

import org.example.backendrl.dto.AlbumSongResponse;
import org.example.backendrl.dto.AlbumSummaryResponse;
import org.example.backendrl.entity.Album;
import org.example.backendrl.entity.AlbumSong;
import org.example.backendrl.entity.Song;
import org.example.backendrl.exception.ConflictException;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.AlbumRepository;
import org.example.backendrl.repository.AlbumSongRepository;
import org.example.backendrl.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlbumSongService {

    private final AlbumSongRepository albumSongRepository;
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;

    public AlbumSongService(
            AlbumSongRepository albumSongRepository,
            AlbumRepository albumRepository,
            SongRepository songRepository
    ) {
        this.albumSongRepository = albumSongRepository;
        this.albumRepository = albumRepository;
        this.songRepository = songRepository;
    }

    public List<AlbumSongResponse> getSongsForAlbum(Long albumId) {

        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException(
                    "Album with id " + albumId + " not found"
            );
        }

        return albumSongRepository
                .findByAlbumIdOrderByDiscNumberAscTrackNumberAsc(albumId)
                .stream()
                .map(AlbumSongResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void addSongToAlbum(
            Long albumId,
            Long songId,
            Integer trackNumber,
            Integer discNumber
    ) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Album with id " + albumId + " not found"
                ));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song with id " + songId + " not found"
                ));

        if (albumSongRepository.existsByAlbumIdAndSongId(
                albumId, songId)) {
            throw new ConflictException(
                    "Song " + songId + " is already associated with album " + albumId
            );
        }

        AlbumSong albumSong = new AlbumSong();

        albumSong.setAlbum(album);
        albumSong.setSong(song);
        albumSong.setTrackNumber(trackNumber);
        albumSong.setDiscNumber(discNumber);

        albumSongRepository.save(albumSong);
    }

    @Transactional
    public void removeSongFromAlbum(
            Long albumId,
            Long songId
    ) {

        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException(
                    "Album with id " + albumId + " not found"
            );
        }

        if (!songRepository.existsById(songId)) {
            throw new ResourceNotFoundException(
                    "Song with id " + songId + " not found"
            );
        }

        AlbumSong albumSong = albumSongRepository
                .findByAlbumIdAndSongId(albumId, songId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song " + songId +
                                " is not associated with album " + albumId
                ));

        albumSongRepository.delete(albumSong);
    }

    public List<AlbumSummaryResponse> getAlbumsForSong(Long songId) {

        if (!songRepository.existsById(songId)) {
            throw new ResourceNotFoundException(
                    "Song with id " + songId + " not found"
            );
        }

        return albumSongRepository.findBySongId(songId)
                .stream()
                .map(AlbumSong::getAlbum)
                .map(AlbumSummaryResponse::fromEntity)
                .toList();
    }
}
