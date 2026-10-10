package org.example.backendrl.repository;

import org.example.backendrl.entity.AlbumRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlbumRatingRepository extends JpaRepository<AlbumRating, Long>, JpaSpecificationExecutor<AlbumRating> {

    List<AlbumRating> findByAlbumId(Long albumId);

    List<AlbumRating> findByUserId(Long userId);

    Optional<AlbumRating> findByAlbumIdAndUserId(Long albumId, Long userId);

    boolean existsByAlbumIdAndUserId(Long albumId, Long userId);

    List<AlbumRating> findByUserIdInOrderByUpdatedAtDesc(List<Long> userIds);

    void deleteByUserId(Long userId);

    void deleteByAlbumId(Long albumId);

    /**
     * Albums ranked by average rating (ties: more ratings first).
     * Every filter is optional (pass null to skip it); genre must be lower-case.
     */
    @Query(value = """
            select a.id as id, avg(r.rating) as average, count(r) as ratingCount
            from AlbumRating r join r.album a
            where (:artistId is null or a.artist.id = :artistId)
              and (:year is null or extract(year from a.releaseDate) = :year)
              and (:genre is null or exists (
                    select ag.id from AlbumGenre ag
                    where ag.album = a and lower(ag.genre.name) = :genre))
            group by a.id
            having count(r) >= :minRatings
            order by avg(r.rating) desc, count(r) desc, a.id asc
            """,
            countQuery = """
            select count(a) from Album a
            where (:artistId is null or a.artist.id = :artistId)
              and (:year is null or extract(year from a.releaseDate) = :year)
              and (:genre is null or exists (
                    select ag.id from AlbumGenre ag
                    where ag.album = a and lower(ag.genre.name) = :genre))
              and (select count(r) from AlbumRating r where r.album = a) >= :minRatings
            """)
    Page<RatingStats> findTopAlbums(
            @Param("genre") String genre,
            @Param("year") Integer year,
            @Param("artistId") Long artistId,
            @Param("minRatings") long minRatings,
            Pageable pageable
    );
}
