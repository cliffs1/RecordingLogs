package org.example.backendrl.repository.spec;

import org.example.backendrl.entity.Song;
import org.springframework.data.jpa.domain.Specification;

public final class SongSpecs {

    private SongSpecs() {
    }

    public static Specification<Song> byAlbum(Long albumId) {
        return (root, query, cb) -> cb.equal(root.get("album").get("id"), albumId);
    }

    public static Specification<Song> titleContains(String title) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.get("title")),
                "%" + title.toLowerCase() + "%"
        );
    }

    public static Specification<Song> minDuration(Integer seconds) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("durationSeconds"), seconds);
    }

    public static Specification<Song> maxDuration(Integer seconds) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("durationSeconds"), seconds);
    }

    public static Specification<Song> onDisc(Integer disc) {
        return (root, query, cb) -> cb.equal(root.get("discNumber"), disc);
    }
}
