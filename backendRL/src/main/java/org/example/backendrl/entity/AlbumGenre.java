package org.example.backendrl.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "album_genres",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"album_id", "genre_id"})
        }
)
public class AlbumGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToOne(optional = false)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    public AlbumGenre() {
    }

    public Long getId() {
        return id;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }
}
