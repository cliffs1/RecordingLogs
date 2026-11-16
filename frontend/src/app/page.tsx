"use client";

import Link from "next/link";
import { useState } from "react";
import { Activity, Mic2, Star, TrendingUp, UserPlus } from "lucide-react";
import AlbumCard from "@/components/AlbumCard";
import ActivityFeed from "@/components/ActivityFeed";
import Cover from "@/components/Cover";
import { SkeletonGrid, SkeletonList } from "@/components/Skeletons";
import { EmptyState, ErrorState } from "@/components/States";
import { useAuth } from "@/context/AuthContext";
import { useLoad } from "@/hooks/useLoad";
import { chartApi, genreApi } from "@/lib/endpoints";
import { albumHref, formatDuration } from "@/lib/format";
import styles from "./home.module.css";

export default function HomePage() {
  const { user } = useAuth();
  const [genre, setGenre] = useState("");
  const [year, setYear] = useState("");
  const [minRatings, setMinRatings] = useState("1");

  const filters = { genre, year: year || undefined, minRatings: minRatings || undefined };
  const genres = useLoad(() => genreApi.list({ size: 100, sort: "name" }), []);
  const albums = useLoad(() => chartApi.topAlbums({ ...filters, size: 8 }), [genre, year, minRatings]);
  const songs = useLoad(() => chartApi.topSongs({ ...filters, size: 10 }), [genre, year, minRatings]);

  return (
    <>
      <section className={styles.hero}>
        <div className="fade-up">
          <p className="eyebrow" style={{ color: "var(--amber-400)" }}>
            Your music diary
          </p>
          <h1>
            Log every record. <em>Rate</em> what matters.
          </h1>
          <p>
            Discover top-rated albums and songs, write reviews and keep curated lists. Follow
            friends and see what they are spinning.
          </p>
          <div className={styles.heroActions}>
            <Link href="/artists" className="btn btn-primary">
              <Mic2 size={18} aria-hidden /> Browse artists
            </Link>
            {!user && (
              <Link href="/register" className="btn btn-secondary">
                <UserPlus size={18} aria-hidden /> Create account
              </Link>
            )}
          </div>
        </div>
        <div className={styles.vinyl} aria-hidden />
      </section>

      <section className={styles.section} aria-labelledby="chart-filters">
        <div className="section-head">
          <h2 id="chart-filters">
            <TrendingUp size={22} aria-hidden style={{ verticalAlign: -3 }} /> Charts
          </h2>
          <div className={styles.filters}>
            <label className="sr-only" htmlFor="f-genre">
              Genre
            </label>
            <select id="f-genre" className="select" value={genre} onChange={(e) => setGenre(e.target.value)}>
              <option value="">All genres</option>
              {genres.data?.content.map((g) => (
                <option key={g.id} value={g.name}>
                  {g.name}
                </option>
              ))}
            </select>
            <label className="sr-only" htmlFor="f-year">
              Year
            </label>
            <input
              id="f-year"
              type="number"
              className="input"
              placeholder="Any year"
              min={1900}
              max={2100}
              value={year}
              onChange={(e) => setYear(e.target.value)}
            />
            <label className="sr-only" htmlFor="f-min">
              Minimum number of ratings
            </label>
            <select id="f-min" className="select" value={minRatings} onChange={(e) => setMinRatings(e.target.value)}>
              <option value="1">≥ 1 rating</option>
              <option value="2">≥ 2 ratings</option>
              <option value="3">≥ 3 ratings</option>
              <option value="5">≥ 5 ratings</option>
            </select>
          </div>
        </div>

        <h3 style={{ marginBottom: 16 }}>Top albums</h3>
        {albums.error ? (
          <ErrorState message={albums.error} onRetry={albums.reload} />
        ) : albums.loading && !albums.data ? (
          <SkeletonGrid count={4} />
        ) : !albums.data?.content.length ? (
          <EmptyState title="No rated albums match these filters" />
        ) : (
          <div className="grid">
            {albums.data.content.map((album) => (
              <AlbumCard
                key={album.albumId}
                albumId={album.albumId}
                artistId={album.artistId}
                title={album.title}
                coverUrl={album.coverUrl}
                artistName={album.artistName}
                releaseDate={album.releaseDate}
                badge={<>#{album.rank}</>}
                footer={
                  <span>
                    <Star size={14} aria-hidden fill="var(--amber-500)" color="var(--amber-500)" style={{ verticalAlign: -2 }} />{" "}
                    <strong style={{ color: "var(--ink-900)" }}>{album.averageRating.toFixed(1)}</strong> ·{" "}
                    {album.ratingCount} ratings
                  </span>
                }
              />
            ))}
          </div>
        )}

        <h3 style={{ margin: "32px 0 16px" }}>Top songs</h3>
        {songs.error ? (
          <ErrorState message={songs.error} onRetry={songs.reload} />
        ) : songs.loading && !songs.data ? (
          <SkeletonList />
        ) : !songs.data?.content.length ? (
          <EmptyState title="No rated songs match these filters" />
        ) : (
          <ol className="list card" style={{ padding: 0 }}>
            {songs.data.content.map((song) => (
              <li key={song.songId} className="list-item">
                <span className={styles.rank}>{song.rank}</span>
                <div className={styles.thumb}>
                  <Cover src={song.coverUrl} alt="" />
                </div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <Link href={albumHref(song.artistId, song.albumId)} className="plain">
                    <strong>{song.title}</strong>
                  </Link>
                  <p className="muted small">
                    <Link href={`/artists/${song.artistId}`}>{song.artistName}</Link> · {song.albumTitle} ·{" "}
                    {formatDuration(song.durationSeconds)}
                  </p>
                </div>
                <span className="score" title={`${song.ratingCount} ratings`}>
                  {song.averageRating.toFixed(1)}
                </span>
              </li>
            ))}
          </ol>
        )}
      </section>

      {user && (
        <section className={styles.section} aria-labelledby="feed-title">
          <div className="section-head">
            <h2 id="feed-title">
              <Activity size={22} aria-hidden style={{ verticalAlign: -3 }} /> From people you follow
            </h2>
          </div>
          <ActivityFeed userId={user.id} />
        </section>
      )}
    </>
  );
}
