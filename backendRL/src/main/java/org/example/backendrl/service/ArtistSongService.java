package org.example.backendrl.service;

import org.example.backendrl.dto.ArtistSummaryResponse;
import org.example.backendrl.dto.SongSummaryResponse;
import org.example.backendrl.entity.Artist;
import org.example.backendrl.entity.Song;
import org.example.backendrl.entity.SongArtist;
import org.example.backendrl.exception.ResourceNotFoundException;
import org.example.backendrl.repository.ArtistRepository;
import org.example.backendrl.repository.ArtistSongRepository;
import org.example.backendrl.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArtistSongService {

    private final ArtistSongRepository artistSongRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;

    public ArtistSongService(
            ArtistSongRepository artistSongRepository,
            ArtistRepository artistRepository,
            SongRepository songRepository
    ) {
        this.artistSongRepository = artistSongRepository;
        this.artistRepository = artistRepository;
        this.songRepository = songRepository;
    }

    public List<SongSummaryResponse> getSongsForArtist(Long artistId) {

        if (!artistRepository.existsById(artistId)) {
            throw new ResourceNotFoundException(
                    "Artist with id " + artistId + " not found"
            );
        }

        return artistSongRepository.findByArtistId(artistId)
                .stream()
                .map(SongArtist::getSong)
                .map(SongSummaryResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void addSongToArtist(Long artistId, Long songId) {

        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artist with id " + artistId + " not found"
                ));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song with id " + songId + " not found"
                ));

        if (artistSongRepository.existsByArtistIdAndSongId(
                artistId, songId)) {
            return;
        }

        SongArtist songArtist = new SongArtist();

        songArtist.setArtist(artist);
        songArtist.setSong(song);

        artistSongRepository.save(songArtist);
    }

    @Transactional
    public void removeSongFromArtist(Long artistId, Long songId) {

        if (!artistRepository.existsById(artistId)) {
            throw new ResourceNotFoundException(
                    "Artist with id " + artistId + " not found"
            );
        }

        if (!songRepository.existsById(songId)) {
            throw new ResourceNotFoundException(
                    "Song with id " + songId + " not found"
            );
        }

        if (!artistSongRepository.existsByArtistIdAndSongId(
                artistId, songId)) {

            throw new ResourceNotFoundException(
                    "Song " + songId +
                            " is not associated with artist " + artistId
            );
        }

        artistSongRepository.deleteByArtistIdAndSongId(
                artistId,
                songId
        );
    }

    public List<ArtistSummaryResponse> getArtistsForSong(Long songId) {

        if (!songRepository.existsById(songId)) {
            throw new ResourceNotFoundException(
                    "Song with id " + songId + " not found"
            );
        }

        return artistSongRepository.findBySongId(songId)
                .stream()
                .map(SongArtist::getArtist)
                .map(ArtistSummaryResponse::fromEntity)
                .toList();
    }
}
