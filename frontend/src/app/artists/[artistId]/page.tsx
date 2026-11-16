"use client";

import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { useState } from "react";
import { Disc3, Mic2, Pencil, Plus, Search, Trash2 } from "lucide-react";
import AlbumCard from "@/components/AlbumCard";
import ConfirmDialog from "@/components/ConfirmDialog";
import GenreChips from "@/components/GenreChips";
import Modal from "@/components/Modal";
import Pagination from "@/components/Pagination";
import { PageLoader } from "@/components/Spinner";
import { SkeletonGrid } from "@/components/Skeletons";
import { EmptyState, ErrorState } from "@/components/States";
import AlbumForm from "@/components/forms/AlbumForm";
import ArtistForm from "@/components/forms/ArtistForm";
import { useAuth } from "@/context/AuthContext";
import { useDebounced } from "@/hooks/useDebounced";
import { useLoad } from "@/hooks/useLoad";
import { albumApi, artistApi } from "@/lib/endpoints";
import { albumHref, initials } from "@/lib/format";
import type { AlbumResponse } from "@/types/api";
import styles from "../artists.module.css";

type Dialog =
  | { kind: "editArtist" | "deleteArtist" | "createAlbum" }
  | { kind: "editAlbum" | "deleteAlbum"; album: AlbumResponse }
  | null;

export default function ArtistPage() {
  const artistId = Number(useParams<{ artistId: string }>().artistId);
  const router = useRouter();
  const { canEditCatalog } = useAuth();
  const [dialog, setDialog] = useState<Dialog>(null);
  const [search, setSearch] = useState("");
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
  const [page, setPage] = useState(0);
  const title = useDebounced(search);

  const artist = useLoad(() => artistApi.get(artistId), [artistId]);
  const albums = useLoad(
    () =>
      albumApi.list(artistId, {
        title,
        releasedFrom: from,
        releasedTo: to,
        page,
        size: 12,
        sort: "releaseDate,desc",
      }),
    [artistId, title, from, to, page],
  );
  const close = () => setDialog(null);

  if (artist.error) return <ErrorState message={artist.error} onRetry={artist.reload} />;
  if (!artist.data) return <PageLoader />;
  const a = artist.data;

  return (
    <>
      <p className="eyebrow" style={{ marginBottom: 16 }}>
        <Link href="/artists">Artists</Link> / {a.name}
      </p>

      <section className={styles.hero}>
        <span className={styles.avatar} aria-hidden>
          {initials(a.name)}
        </span>
        <div className={styles.heroText}>
          <h1>{a.name}</h1>
          <GenreChips
            deps={[artistId]}
            load={() => artistApi.genres(artistId, { size: 50 })}
            add={(genreId) => artistApi.addGenre(artistId, genreId)}
            remove={(genreId) => artistApi.removeGenre(artistId, genreId)}
            canEdit={canEditCatalog}
          />
          {a.biography && <p className={styles.biography}>{a.biography}</p>}
          {canEditCatalog && (
            <div className="row">
              <button type="button" className="btn btn-secondary btn-sm" onClick={() => setDialog({ kind: "editArtist" })}>
                <Pencil size={14} aria-hidden /> Edit artist
              </button>
              <button type="button" className="btn btn-ghost btn-sm" onClick={() => setDialog({ kind: "deleteArtist" })}>
                <Trash2 size={14} aria-hidden /> Delete
              </button>
            </div>
          )}
        </div>
      </section>

      <section>
        <div className="section-head">
          <h2>
            <Disc3 size={22} aria-hidden style={{ verticalAlign: -3 }} /> Discography
          </h2>
          {canEditCatalog && (
            <button type="button" className="btn btn-primary" onClick={() => setDialog({ kind: "createAlbum" })}>
              <Plus size={18} aria-hidden /> Add album
            </button>
          )}
        </div>

        <div className="toolbar">
          <div className="search">
            <Search size={18} aria-hidden />
            <label htmlFor="album-search" className="sr-only">
              Search albums
            </label>
            <input
              id="album-search"
              type="search"
              className="input"
              placeholder="Search albums…"
              value={search}
              onChange={(event) => {
                setSearch(event.target.value);
                setPage(0);
              }}
            />
          </div>
          <div className="field">
            <label className="label" htmlFor="from">
              Released from
            </label>
            <input id="from" type="date" className="input" value={from} onChange={(e) => setFrom(e.target.value)} />
          </div>
          <div className="field">
            <label className="label" htmlFor="to">
              Released to
            </label>
            <input id="to" type="date" className="input" value={to} onChange={(e) => setTo(e.target.value)} />
          </div>
        </div>

        {albums.error ? (
          <ErrorState message={albums.error} onRetry={albums.reload} />
        ) : albums.loading && !albums.data ? (
          <SkeletonGrid count={4} />
        ) : !albums.data?.content.length ? (
          <EmptyState title="No albums found" icon={<Disc3 size={32} aria-hidden />} />
        ) : (
          <div className="grid">
            {albums.data.content.map((album) => (
              <AlbumCard
                key={album.id}
                albumId={album.id}
                artistId={artistId}
                title={album.title}
                coverUrl={album.coverUrl}
                releaseDate={album.releaseDate}
                artistName={a.name}
                actions={
                  canEditCatalog && (
                    <>
                      <button
                        type="button"
                        className="btn btn-ghost btn-sm"
                        onClick={() => setDialog({ kind: "editAlbum", album })}
                      >
                        <Pencil size={14} aria-hidden /> Edit
                      </button>
                      <button
                        type="button"
                        className="btn btn-ghost btn-sm"
                        onClick={() => setDialog({ kind: "deleteAlbum", album })}
                      >
                        <Trash2 size={14} aria-hidden /> Delete
                      </button>
                    </>
                  )
                }
              />
            ))}
          </div>
        )}
        <Pagination data={albums.data} onPage={setPage} />
      </section>

      <Modal open={dialog?.kind === "editArtist"} onClose={close} title="Edit artist" icon={<Mic2 size={20} />}>
        {dialog?.kind === "editArtist" && (
          <ArtistForm
            artist={a}
            onCancel={close}
            onDone={(updated) => {
              close();
              artist.setData(updated);
            }}
          />
        )}
      </Modal>

      <Modal
        open={dialog?.kind === "createAlbum" || dialog?.kind === "editAlbum"}
        onClose={close}
        title={dialog?.kind === "editAlbum" ? "Edit album" : `New album by ${a.name}`}
        icon={<Disc3 size={20} />}
      >
        {(dialog?.kind === "createAlbum" || dialog?.kind === "editAlbum") && (
          <AlbumForm
            artistId={artistId}
            album={dialog.kind === "editAlbum" ? dialog.album : undefined}
            onCancel={close}
            onDone={(saved) => {
              const created = dialog.kind === "createAlbum";
              close();
              if (created) router.push(albumHref(artistId, saved.id));
              else albums.reload();
            }}
          />
        )}
      </Modal>

      <ConfirmDialog
        open={dialog?.kind === "deleteArtist"}
        onClose={close}
        title="Delete artist?"
        message={
          <>
            <strong>{a.name}</strong> and all of their albums and songs will be deleted.
          </>
        }
        successMessage="Artist deleted"
        onConfirm={() => artistApi.remove(artistId)}
        onDone={() => router.push("/artists")}
      />

      <ConfirmDialog
        open={dialog?.kind === "deleteAlbum"}
        onClose={close}
        title="Delete album?"
        message={
          dialog?.kind === "deleteAlbum" && (
            <>
              <strong>{dialog.album.title}</strong>, its songs, ratings and reviews will be deleted.
            </>
          )
        }
        successMessage="Album deleted"
        onConfirm={() =>
          dialog?.kind === "deleteAlbum" ? albumApi.remove(artistId, dialog.album.id) : Promise.resolve()
        }
        onDone={albums.reload}
      />
    </>
  );
}
