"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { Disc3, Mic2, Music2, Tags } from "lucide-react";
import AlbumCard from "@/components/AlbumCard";
import Pagination from "@/components/Pagination";
import { PageLoader } from "@/components/Spinner";
import { SkeletonGrid, SkeletonList } from "@/components/Skeletons";
import { EmptyState, ErrorState } from "@/components/States";
import { useLoad } from "@/hooks/useLoad";
import { genreApi } from "@/lib/endpoints";
import { albumHref, formatDuration } from "@/lib/format";

type Tab = "albums" | "artists" | "songs";

const TABS = [
  { id: "albums" as const, label: "Albums", icon: Disc3 },
  { id: "artists" as const, label: "Artists", icon: Mic2 },
  { id: "songs" as const, label: "Songs", icon: Music2 },
];

export default function GenrePage() {
  const id = Number(useParams<{ genreId: string }>().genreId);
  const [tab, setTab] = useState<Tab>("albums");
  const [page, setPage] = useState(0);
  const genre = useLoad(() => genreApi.get(id), [id]);

  const albums = useLoad(() => genreApi.albums(id, { page, size: 12 }), [id, page], tab === "albums");
  const artists = useLoad(() => genreApi.artists(id, { page, size: 20 }), [id, page], tab === "artists");
  const songs = useLoad(() => genreApi.songs(id, { page, size: 20 }), [id, page], tab === "songs");

  if (genre.error) return <ErrorState message={genre.error} onRetry={genre.reload} />;
  if (!genre.data) return <PageLoader />;

  const current = tab === "albums" ? albums : tab === "artists" ? artists : songs;

  return (
    <>
      <header className="page-head">
        <div>
          <p className="eyebrow">
            <Link href="/genres">Genres</Link> / {genre.data.name}
          </p>
          <h1>
            <Tags size={30} aria-hidden style={{ verticalAlign: -4, color: "var(--violet-600)" }} />{" "}
            {genre.data.name}
          </h1>
        </div>
      </header>

      <div className="tabs" role="tablist">
        {TABS.map(({ id: tabId, label, icon: Icon }) => (
          <button
            key={tabId}
            type="button"
            role="tab"
            className="tab"
            aria-selected={tab === tabId}
            onClick={() => {
              setTab(tabId);
              setPage(0);
            }}
          >
            <Icon size={16} aria-hidden /> {label}
          </button>
        ))}
      </div>

      {current.error ? (
        <ErrorState message={current.error} onRetry={current.reload} />
      ) : current.loading ? (
        tab === "albums" ? <SkeletonGrid count={4} /> : <SkeletonList />
      ) : !current.data?.content.length ? (
        <EmptyState title={`No ${tab} in this genre yet`} />
      ) : tab === "albums" ? (
        <div className="grid">
          {albums.data!.content.map((album) => (
            <AlbumCard
              key={album.id}
              albumId={album.id}
              artistId={album.artistId}
              title={album.title}
              coverUrl={album.coverUrl}
              artistName={album.artistName}
              releaseDate={album.releaseDate}
            />
          ))}
        </div>
      ) : tab === "artists" ? (
        <ul className="list card" style={{ padding: 0 }}>
          {artists.data!.content.map((artist) => (
            <li key={artist.id} className="list-item">
              <Mic2 size={18} aria-hidden color="var(--violet-600)" />
              <Link href={`/artists/${artist.id}`}>{artist.name}</Link>
            </li>
          ))}
        </ul>
      ) : (
        <ul className="list card" style={{ padding: 0 }}>
          {songs.data!.content.map((song) => (
            <li key={song.id} className="list-item">
              <Music2 size={18} aria-hidden color="var(--violet-600)" />
              <div style={{ flex: 1, minWidth: 0 }}>
                <strong>{song.title}</strong>
                <p className="muted small">
                  <Link href={`/artists/${song.artistId}`}>{song.artistName}</Link> ·{" "}
                  <Link href={albumHref(song.artistId, song.albumId)}>{song.albumTitle}</Link>
                </p>
              </div>
              <span className="muted small">{formatDuration(song.durationSeconds)}</span>
            </li>
          ))}
        </ul>
      )}
      <Pagination data={current.data} onPage={setPage} />
    </>
  );
}
