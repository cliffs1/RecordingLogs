package org.example.backendrl.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "song_genres",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"song_id", "genre_id"})
        }
)
public class SongGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "song_id")
    private Song song;

    @ManyToOne(optional = false)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    public SongGenre() {
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

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }
}