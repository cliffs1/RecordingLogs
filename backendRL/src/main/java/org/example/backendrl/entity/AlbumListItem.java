package org.example.backendrl.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "album_list_items",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"album_list_id", "album_id"})
        }
)
public class AlbumListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "album_list_id")
    private AlbumList albumList;

    @ManyToOne(optional = false)
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(nullable = false)
    private Integer position;

    public AlbumListItem() {
    }

    public Long getId() {
        return id;
    }

    public AlbumList getAlbumList() {
        return albumList;
    }

    public void setAlbumList(AlbumList albumList) {
        this.albumList = albumList;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
