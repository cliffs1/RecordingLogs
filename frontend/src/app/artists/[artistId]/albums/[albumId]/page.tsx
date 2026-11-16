"use client";

import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { useState } from "react";
import {
  CalendarDays,
  Disc3,
  Heart,
  ListPlus,
  MessageSquarePlus,
  MessageSquareText,
  Music2,
  Pencil,
  Plus,
  Star,
  Trash2,
} from "lucide-react";
import ConfirmDialog from "@/components/ConfirmDialog";
import Cover from "@/components/Cover";
import GenreChips from "@/components/GenreChips";
import Modal from "@/components/Modal";
import MyRating from "@/components/MyRating";
import Pagination from "@/components/Pagination";
import ReviewCard from "@/components/ReviewCard";
import ReviewModal from "@/components/ReviewModal";
import SongModal from "@/components/SongModal";
import { PageLoader } from "@/components/Spinner";
import Spinner from "@/components/Spinner";
import { SkeletonList } from "@/components/Skeletons";
import { EmptyState, ErrorState } from "@/components/States";
import { useToast } from "@/components/Toast";
import AddToListForm from "@/components/forms/AddToListForm";
import AlbumForm from "@/components/forms/AlbumForm";
import ReviewForm from "@/components/forms/ReviewForm";
import SongForm from "@/components/forms/SongForm";
import { useAuth } from "@/context/AuthContext";
import { useLoad } from "@/hooks/useLoad";
import {
  albumApi,
  albumRatingApi,
  artistApi,
  reviewApi,
  songApi,
  userApi,
} from "@/lib/endpoints";
import { errorMessage, formatDate, formatDuration, timeAgo } from "@/lib/format";
import type { AlbumReviewResponse, SongResponse } from "@/types/api";
import styles from "./album.module.css";

type Dialog =
  | { kind: "editAlbum" | "deleteAlbum" | "createSong" | "addToList" | "createReview" | "ratings" }
  | { kind: "editSong" | "deleteSong"; song: SongResponse }
  | { kind: "editReview" | "deleteReview"; review: AlbumReviewResponse }
  | null;

export default function AlbumPage() {
  const params = useParams<{ artistId: string; albumId: string }>();
  const artistId = Number(params.artistId);
  const albumId = Number(params.albumId);
  const router = useRouter();
  const toast = useToast();
  const { user, canEditCatalog, isAdmin } = useAuth();
  const [dialog, setDialog] = useState<Dialog>(null);
  const [songId, setSongId] = useState<number | null>(null);
  const [reviewId, setReviewId] = useState<number | null>(null);
  const [reviewPage, setReviewPage] = useState(0);
  const [favBusy, setFavBusy] = useState(false);

  const album = useLoad(() => albumApi.get(artistId, albumId), [artistId, albumId]);
  const artist = useLoad(() => artistApi.get(artistId), [artistId]);
  const songs = useLoad(() => songApi.list(artistId, albumId, { size: 100 }), [artistId, albumId]);
  const ratings = useLoad(() => albumRatingApi.list(albumId, { size: 100 }), [albumId]);
  const reviews = useLoad(
    () => reviewApi.byAlbum(albumId, { page: reviewPage, size: 5 }),
    [albumId, reviewPage],
  );
  const favorites = useLoad(
    () => userApi.favorites(user!.id, { albumTitle: album.data?.title, size: 100 }),
    [user?.id, album.data?.title],
    !!user && !!album.data,
  );

  const close = () => setDialog(null);

  if (album.error) return <ErrorState message={album.error} onRetry={album.reload} />;
  if (!album.data) return <PageLoader />;
  const a = album.data;

  const ratingList = ratings.data?.content ?? [];
  const average = ratingList.length
    ? ratingList.reduce((sum, rating) => sum + rating.rating, 0) / ratingList.length
    : null;
  const isFavorite = !!favorites.data?.content.some((fav) => fav.albumId === albumId);
  const songList = songs.data?.content ?? [];
  const totalSeconds = songList.reduce((sum, song) => sum + (song.durationSeconds ?? 0), 0);
  const nextTrack = songList.length ? Math.max(...songList.map((song) => song.trackNumber)) + 1 : 1;
  const myReview = user && reviews.data?.content.find((review) => review.userId === user.id);

  async function toggleFavorite() {
    if (!user) {
      router.push("/login");
      return;
    }
    setFavBusy(true);
    try {
      if (isFavorite) {
        await userApi.removeFavorite(user.id, albumId);
        toast.success("Removed from favorites");
      } else {
        await userApi.addFavorite(user.id, albumId);
        toast.success("Added to favorites");
      }
      favorites.reload();
    } catch (error) {
      toast.error(errorMessage(error));
    } finally {
      setFavBusy(false);
    }
  }

  // Group tracks by disc number for the tracklist.
  const discs = new Map<number, SongResponse[]>();
  for (const song of songList) {
    const disc = song.discNumber ?? 1;
    discs.set(disc, [...(discs.get(disc) ?? []), song]);
  }

  return (
    <>
      <p className="eyebrow" style={{ marginBottom: 16 }}>
        <Link href="/artists">Artists</Link> / <Link href={`/artists/${artistId}`}>{artist.data?.name ?? "…"}</Link> /{" "}
        {a.title}
      </p>

      <section className={styles.hero}>
        <div className={styles.coverWrap}>
          <Cover src={a.coverUrl} alt={`Cover of ${a.title}`} />
        </div>

        <div className={styles.info}>
          <div>
            <p className="eyebrow">Album</p>
            <h1>{a.title}</h1>
            <p className="muted" style={{ marginTop: 8 }}>
              <Link href={`/artists/${artistId}`}>{artist.data?.name}</Link>
              {" · "}
              <CalendarDays size={14} aria-hidden style={{ verticalAlign: -2 }} /> {formatDate(a.releaseDate)}
              {" · "}
              {songList.length} tracks, {formatDuration(totalSeconds)}
            </p>
          </div>

          <GenreChips
            deps={[albumId]}
            load={() => albumApi.genres(albumId, { size: 50 })}
            add={(genreId) => albumApi.addGenre(albumId, genreId)}
            remove={(genreId) => albumApi.removeGenre(albumId, genreId)}
            canEdit={canEditCatalog}
          />

          <div className={styles.stats}>
            <span className={`score ${styles.big}`}>{average != null ? average.toFixed(1) : "–"}</span>
            <div>
              <strong>Average rating</strong>
              <p className="muted small">
                {ratings.data?.totalElements ?? 0} ratings ·{" "}
                <button
                  type="button"
                  className="btn btn-ghost btn-sm"
                  style={{ padding: "0 4px", minHeight: 0 }}
                  onClick={() => setDialog({ kind: "ratings" })}
                >
                  see all
                </button>
              </p>
            </div>
          </div>

          <MyRating targetId={albumId} ratingApi={albumRatingApi} onChange={ratings.reload} />

          <div className="row">
            <button
              type="button"
              className={`btn btn-secondary ${styles.favorite}`}
              aria-pressed={isFavorite}
              onClick={toggleFavorite}
              disabled={favBusy}
            >
              {favBusy ? <Spinner size={16} /> : <Heart size={16} aria-hidden />}
              {isFavorite ? "Favorite" : "Add to favorites"}
            </button>
            {user && (
              <button type="button" className="btn btn-secondary" onClick={() => setDialog({ kind: "addToList" })}>
                <ListPlus size={16} aria-hidden /> Add to list
              </button>
            )}
            {canEditCatalog && (
              <>
                <button type="button" className="btn btn-ghost" onClick={() => setDialog({ kind: "editAlbum" })}>
                  <Pencil size={16} aria-hidden /> Edit
                </button>
                <button type="button" className="btn btn-ghost" onClick={() => setDialog({ kind: "deleteAlbum" })}>
                  <Trash2 size={16} aria-hidden /> Delete
                </button>
              </>
            )}
          </div>
        </div>
      </section>

      <div className={styles.columns}>
        <section aria-labelledby="tracklist">
          <div className="section-head">
            <h2 id="tracklist">
              <Music2 size={22} aria-hidden style={{ verticalAlign: -3 }} /> Tracklist
            </h2>
            {canEditCatalog && (
              <button type="button" className="btn btn-primary btn-sm" onClick={() => setDialog({ kind: "createSong" })}>
                <Plus size={16} aria-hidden /> Add song
              </button>
            )}
          </div>
          {songs.error ? (
            <ErrorState message={songs.error} onRetry={songs.reload} />
          ) : songs.loading && !songs.data ? (
            <SkeletonList />
          ) : !songList.length ? (
            <EmptyState title="No songs yet" icon={<Music2 size={32} aria-hidden />} />
          ) : (
            <div className="card" style={{ padding: 0, overflow: "hidden" }}>
              {[...discs.entries()].map(([disc, tracks]) => (
                <div key={disc}>
                  {discs.size > 1 && <div className={styles.disc}>Disc {disc}</div>}
                  {tracks.map((song) => (
                    <div key={song.id} className={styles.track}>
                      <span className={styles.trackNo}>{song.trackNumber}</span>
                      <button type="button" className={styles.trackTitle} onClick={() => setSongId(song.id)}>
                        {song.title}
                      </button>
                      <span className="muted small">{formatDuration(song.durationSeconds)}</span>
                      <span className={styles.trackActions}>
                        <button
                          type="button"
                          className="btn btn-ghost btn-icon btn-sm"
                          onClick={() => setSongId(song.id)}
                          aria-label={`Rate ${song.title}`}
                          title="Details and rating"
                        >
                          <Star size={14} />
                        </button>
                        {canEditCatalog && (
                          <>
                            <button
                              type="button"
                              className="btn btn-ghost btn-icon btn-sm"
                              onClick={() => setDialog({ kind: "editSong", song })}
                              aria-label={`Edit ${song.title}`}
                            >
                              <Pencil size={14} />
                            </button>
                            <button
                              type="button"
                              className="btn btn-ghost btn-icon btn-sm"
                              onClick={() => setDialog({ kind: "deleteSong", song })}
                              aria-label={`Delete ${song.title}`}
                            >
                              <Trash2 size={14} />
                            </button>
                          </>
                        )}
                      </span>
                    </div>
                  ))}
                </div>
              ))}
            </div>
          )}
        </section>

        <section aria-labelledby="reviews">
          <div className="section-head">
            <h2 id="reviews">
              <MessageSquareText size={22} aria-hidden style={{ verticalAlign: -3 }} /> Reviews
            </h2>
            {user && !myReview && (
              <button type="button" className="btn btn-primary btn-sm" onClick={() => setDialog({ kind: "createReview" })}>
                <MessageSquarePlus size={16} aria-hidden /> Write review
              </button>
            )}
          </div>
          {reviews.error ? (
            <ErrorState message={reviews.error} onRetry={reviews.reload} />
          ) : reviews.loading && !reviews.data ? (
            <SkeletonList count={3} />
          ) : !reviews.data?.content.length ? (
            <EmptyState title="No reviews yet">
              {!user && (
                <p>
                  <Link href="/login">Log in</Link> to write the first one.
                </p>
              )}
            </EmptyState>
          ) : (
            <div className="stack">
              {reviews.data.content.map((review) => (
                <ReviewCard
                  key={review.id}
                  review={review}
                  showAlbum={false}
                  canModify={!!user && (user.id === review.userId || isAdmin)}
                  onOpen={() => setReviewId(review.id)}
                  onEdit={() => setDialog({ kind: "editReview", review })}
                  onDelete={() => setDialog({ kind: "deleteReview", review })}
                />
              ))}
            </div>
          )}
          <Pagination data={reviews.data} onPage={setReviewPage} />
        </section>
      </div>

      {/* ----- Modals ----- */}
      <SongModal artistId={artistId} albumId={albumId} songId={songId} onClose={() => setSongId(null)} />
      <ReviewModal reviewId={reviewId} onClose={() => setReviewId(null)} />

      <Modal open={dialog?.kind === "ratings"} onClose={close} title={`Ratings for ${a.title}`} icon={<Star size={20} />}>
        {!ratingList.length ? (
          <p className="muted">No ratings yet.</p>
        ) : (
          <ul className="list">
            {ratingList.map((rating) => (
              <li key={rating.id} className="list-item" style={{ padding: "8px 0" }}>
                <span className="score" style={{ minWidth: 36, height: 36, fontSize: 14 }}>
                  {rating.rating}
                </span>
                <Link href={`/users/${rating.userId}`} onClick={close}>
                  {rating.username}
                </Link>
                <span className="spacer" />
                <span className="muted small">{timeAgo(rating.updatedAt)}</span>
              </li>
            ))}
          </ul>
        )}
      </Modal>

      <Modal open={dialog?.kind === "editAlbum"} onClose={close} title="Edit album" icon={<Disc3 size={20} />}>
        {dialog?.kind === "editAlbum" && (
          <AlbumForm
            artistId={artistId}
            album={a}
            onCancel={close}
            onDone={(updated) => {
              close();
              album.setData(updated);
              favorites.reload();
            }}
          />
        )}
      </Modal>

      <Modal
        open={dialog?.kind === "createSong" || dialog?.kind === "editSong"}
        onClose={close}
        title={dialog?.kind === "editSong" ? "Edit song" : "Add song"}
        description={a.title}
        icon={<Music2 size={20} />}
      >
        {(dialog?.kind === "createSong" || dialog?.kind === "editSong") && (
          <SongForm
            artistId={artistId}
            albumId={albumId}
            song={dialog.kind === "editSong" ? dialog.song : undefined}
            nextTrack={nextTrack}
            onCancel={close}
            onDone={() => {
              close();
              songs.reload();
            }}
          />
        )}
      </Modal>

      <Modal
        open={dialog?.kind === "createReview" || dialog?.kind === "editReview"}
        onClose={close}
        title={dialog?.kind === "editReview" ? "Edit review" : "Write a review"}
        description={a.title}
        icon={<MessageSquarePlus size={20} />}
        size="lg"
      >
        {(dialog?.kind === "createReview" || dialog?.kind === "editReview") && (
          <ReviewForm
            albumId={albumId}
            review={dialog.kind === "editReview" ? dialog.review : undefined}
            onCancel={close}
            onDone={() => {
              close();
              reviews.reload();
            }}
          />
        )}
      </Modal>

      <Modal open={dialog?.kind === "addToList"} onClose={close} title="Add to list" description={a.title} icon={<ListPlus size={20} />}>
        {dialog?.kind === "addToList" && user && (
          <AddToListForm userId={user.id} albumId={albumId} onCancel={close} onDone={close} />
        )}
      </Modal>

      <ConfirmDialog
        open={dialog?.kind === "deleteAlbum"}
        onClose={close}
        title="Delete album?"
        message={
          <>
            <strong>{a.title}</strong>, its songs, ratings and reviews will be deleted.
          </>
        }
        successMessage="Album deleted"
        onConfirm={() => albumApi.remove(artistId, albumId)}
        onDone={() => router.push(`/artists/${artistId}`)}
      />

      <ConfirmDialog
        open={dialog?.kind === "deleteSong"}
        onClose={close}
        title="Delete song?"
        message={dialog?.kind === "deleteSong" && <>“{dialog.song.title}” and its ratings will be deleted.</>}
        successMessage="Song deleted"
        onConfirm={() =>
          dialog?.kind === "deleteSong" ? songApi.remove(artistId, albumId, dialog.song.id) : Promise.resolve()
        }
        onDone={songs.reload}
      />

      <ConfirmDialog
        open={dialog?.kind === "deleteReview"}
        onClose={close}
        title="Delete review?"
        message={dialog?.kind === "deleteReview" && <>“{dialog.review.title}” will be permanently deleted.</>}
        successMessage="Review deleted"
        onConfirm={() => (dialog?.kind === "deleteReview" ? reviewApi.remove(dialog.review.id) : Promise.resolve())}
        onDone={reviews.reload}
      />
    </>
  );
}
