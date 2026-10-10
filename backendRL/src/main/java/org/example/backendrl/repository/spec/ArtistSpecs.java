package org.example.backendrl.repository.spec;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.example.backendrl.entity.Artist;
import org.example.backendrl.entity.ArtistGenre;
import org.springframework.data.jpa.domain.Specification;

public final class ArtistSpecs {

    private ArtistSpecs() {
    }

    public static Specification<Artist> nameContains(String name) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.get("name")),
                "%" + name.toLowerCase() + "%"
        );
    }

    public static Specification<Artist> hasGenre(String genre) {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<ArtistGenre> artistGenre = sub.from(ArtistGenre.class);

            sub.select(artistGenre.get("id")).where(
                    cb.equal(artistGenre.get("artist"), root),
                    cb.equal(
                            cb.lower(artistGenre.get("genre").get("name")),
                            genre.toLowerCase()
                    )
            );

            return cb.exists(sub);
        };
    }
}
