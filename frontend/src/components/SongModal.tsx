"use client";

import Link from "next/link";
import { Music2 } from "lucide-react";
import GenreChips from "./GenreChips";
import Modal from "./Modal";
import MyRating from "./MyRating";
import Spinner from "./Spinner";
import { ErrorState } from "./States";
import { useAuth } from "@/context/AuthContext";
import { useLoad } from "@/hooks/useLoad";
import { songApi, songRatingApi } from "@/lib/endpoints";
import { formatDate, formatDuration, timeAgo } from "@/lib/format";

/**
 * Song details: GET one song, its genres (attach/detach), the user's own rating
 * and the list of all ratings for the song.
 */
export default function SongModal({
  artistId,
  albumId,
  songId,
  onClose,
}: {
  artistId: number;
  albumId: number;
  songId: number | null;
  onClose: () => void;
}) {
  const { canEditCatalog } = useAuth();
  const open = songId != null;
  const song = useLoad(() => songApi.get(artistId, albumId, songId!), [songId], open);
  const ratings = useLoad(() => songRatingApi.list(songId!, { size: 100 }), [songId], open);
  const s = song.data?.id === songId ? song.data : undefined;

  const list = ratings.data?.content ?? [];
  const average = list.length ? list.reduce((sum, r) => sum + r.rating, 0) / list.length : null;

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={s?.title ?? "Song"}
      description={
        s
          ? `Track ${s.trackNumber}${s.discNumber ? ` · Disc ${s.discNumber}` : ""} · ${formatDuration(s.durationSeconds)}`
          : undefined
      }
      icon={<Music2 size={20} />}
    >
      {song.error ? (
        <ErrorState message={song.error} onRetry={song.reload} />
      ) : !s ? (
        <Spinner label="Loading song…" />
      ) : (
        <div className="stack">
          <div className="row" style={{ justifyContent: "space-between" }}>
            <span className="muted small">Originally released: {formatDate(s.originalReleaseDate)}</span>
            {average != null && (
              <span className="row" style={{ gap: 8 }}>
                <span className="score">{average.toFixed(1)}</span>
                <span className="muted small">{ratings.data?.totalElements} ratings</span>
              </span>
            )}
          </div>

          <div className="field">
            <span className="label">Genres</span>
            <GenreChips
              deps={[s.id]}
              load={() => songApi.genres(s.id, { size: 50 })}
              add={(genreId) => songApi.addGenre(s.id, genreId)}
              remove={(genreId) => songApi.removeGenre(s.id, genreId)}
              canEdit={canEditCatalog}
            />
          </div>

          <MyRating key={s.id} targetId={s.id} ratingApi={songRatingApi} onChange={ratings.reload} compact />

          <div className="field">
            <span className="label">Recent ratings</span>
            {ratings.loading && !ratings.data ? (
              <Spinner size={16} />
            ) : !list.length ? (
              <span className="muted small">No ratings yet — be the first.</span>
            ) : (
              <ul className="list">
                {list.slice(0, 10).map((rating) => (
                  <li key={rating.id} className="list-item" style={{ padding: "8px 0" }}>
                    <span className="score" style={{ minWidth: 36, height: 36, fontSize: 14 }}>
                      {rating.rating}
                    </span>
                    <Link href={`/users/${rating.userId}`} onClick={onClose}>
                      {rating.username}
                    </Link>
                    <span className="spacer" />
                    <span className="muted small">{timeAgo(rating.updatedAt)}</span>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      )}
    </Modal>
  );
}
