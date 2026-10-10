package org.example.backendrl.repository.spec;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * Generic filter building blocks for query parameters.
 * Attributes are given as dotted paths, e.g. "album.id" or "user.username".
 * Every builder returns null when the filter value is missing,
 * and {@link #all} skips those, so optional parameters need no if-checks.
 */
public final class Specs {

    private Specs() {
    }

    @SafeVarargs
    public static <T> Specification<T> all(Specification<T>... specs) {
        return Specification.allOf(
                Arrays.stream(specs).filter(Objects::nonNull).toList()
        );
    }

    public static <T> Specification<T> equal(String path, Object value) {
        if (value == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(path(root, path), value);
    }

    /**
     * Case-insensitive "contains" match on a text attribute.
     */
    public static <T> Specification<T> contains(String path, String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        return (root, query, cb) -> cb.like(
                cb.lower(path(root, path)),
                "%" + text.toLowerCase() + "%"
        );
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> atLeast(String path, Y value) {
        if (value == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(path(root, path), value);
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> atMost(String path, Y value) {
        if (value == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(path(root, path), value);
    }

    /**
     * Timestamp attribute on or after the start of the given day.
     */
    public static <T> Specification<T> onOrAfter(String path, LocalDate date) {
        return date == null ? null : atLeast(path, date.atStartOfDay());
    }

    /**
     * Timestamp attribute on or before the end of the given day.
     */
    public static <T> Specification<T> onOrBefore(String path, LocalDate date) {
        if (date == null) {
            return null;
        }

        LocalDateTime nextDay = date.plusDays(1).atStartOfDay();

        return (root, query, cb) -> cb.lessThan(path(root, path), nextDay);
    }

    @SuppressWarnings("unchecked")
    private static <Y> Path<Y> path(Root<?> root, String dottedPath) {
        Path<?> path = root;

        for (String part : dottedPath.split("\\.")) {
            path = path.get(part);
        }

        return (Path<Y>) path;
    }
}
