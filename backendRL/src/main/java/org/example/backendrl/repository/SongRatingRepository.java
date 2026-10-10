package org.example.backendrl.repository;

import org.example.backendrl.entity.SongRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SongRatingRepository extends JpaRepository<SongRating, Long>, JpaSpecificationExecutor<SongRating> {

    List<SongRating> findBySongId(Long songId);

    List<SongRating> findByUserId(Long userId);

    Optional<SongRating> findBySongIdAndUserId(Long songId, Long userId);

    boolean existsBySongIdAndUserId(Long songId, Long userId);

    List<SongRating> findByUserIdInOrderByUpdatedAtDesc(List<Long> userIds);

    void deleteByUserId(Long userId);

    void deleteBySongId(Long songId);

    /**
     * Songs ranked by average rating (ties: more ratings first).
     * Every filter is optional (pass null to skip it); genre must be lower-case.
     */
    @Query(value = """
            select s.id as id, avg(r.rating) as average, count(r) as ratingCount
            from SongRating r join r.song s
            where (:artistId is null or s.album.artist.id = :artistId)
              and (:year is null or extract(year from s.album.releaseDate) = :year)
              and (:genre is null or exists (
                    select sg.id from SongGenre sg
                    where sg.song = s and lower(sg.genre.name) = :genre))
            group by s.id
            having count(r) >= :minRatings
            order by avg(r.rating) desc, count(r) desc, s.id asc
            """,
            countQuery = """
            select count(s) from Song s
            where (:artistId is null or s.album.artist.id = :artistId)
              and (:year is null or extract(year from s.album.releaseDate) = :year)
              and (:genre is null or exists (
                    select sg.id from SongGenre sg
                    where sg.song = s and lower(sg.genre.name) = :genre))
              and (select count(r) from SongRating r where r.song = s) >= :minRatings
            """)
    Page<RatingStats> findTopSongs(
            @Param("genre") String genre,
            @Param("year") Integer year,
            @Param("artistId") Long artistId,
            @Param("minRatings") long minRatings,
            Pageable pageable
    );
}
