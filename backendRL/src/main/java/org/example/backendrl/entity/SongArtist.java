package org.example.backendrl.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "song_artists",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"song_id", "artist_id"})
        }
)
public class SongArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "song_id")
    private Song song;

    @ManyToOne(optional = false)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    public SongArtist() {
    }

    public Long getId() {
        return id;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
