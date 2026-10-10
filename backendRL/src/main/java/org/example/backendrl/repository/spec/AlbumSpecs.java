package org.example.backendrl.repository.spec;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.example.backendrl.entity.Album;
import org.example.backendrl.entity.AlbumGenre;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class AlbumSpecs {

    private AlbumSpecs() {
    }

    public static Specification<Album> byArtist(Long artistId) {
        return (root, query, cb) -> cb.equal(root.get("artist").get("id"), artistId);
    }

    public static Specification<Album> titleContains(String title) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.get("title")),
                "%" + title.toLowerCase() + "%"
        );
    }

    public static Specification<Album> releasedFrom(LocalDate from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("releaseDate"), from);
    }

    public static Specification<Album> releasedTo(LocalDate to) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("releaseDate"), to);
    }

    public static Specification<Album> hasGenre(String genre) {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<AlbumGenre> albumGenre = sub.from(AlbumGenre.class);

            sub.select(albumGenre.get("id")).where(
                    cb.equal(albumGenre.get("album"), root),
                    cb.equal(
                            cb.lower(albumGenre.get("genre").get("name")),
                            genre.toLowerCase()
                    )
            );

            return cb.exists(sub);
        };
    }
}
