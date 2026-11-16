"use client";

import Link from "next/link";
import { useState } from "react";
import { ArrowUpDown, Mic2, Pencil, Plus, Search, Trash2 } from "lucide-react";
import ConfirmDialog from "@/components/ConfirmDialog";
import Modal from "@/components/Modal";
import Pagination from "@/components/Pagination";
import { SkeletonCards } from "@/components/Skeletons";
import { EmptyState, ErrorState } from "@/components/States";
import ArtistForm from "@/components/forms/ArtistForm";
import { useAuth } from "@/context/AuthContext";
import { useDebounced } from "@/hooks/useDebounced";
import { useLoad } from "@/hooks/useLoad";
import { artistApi, genreApi } from "@/lib/endpoints";
import { initials } from "@/lib/format";
import type { ArtistResponse } from "@/types/api";
import styles from "./artists.module.css";

type Dialog = { kind: "create" } | { kind: "edit" | "delete"; artist: ArtistResponse } | null;

export default function ArtistsPage() {
  const { canEditCatalog } = useAuth();
  const [search, setSearch] = useState("");
  const [genre, setGenre] = useState("");
  const [sort, setSort] = useState("name,asc");
  const [page, setPage] = useState(0);
  const [dialog, setDialog] = useState<Dialog>(null);
  const name = useDebounced(search);

  const genres = useLoad(() => genreApi.list({ size: 100, sort: "name" }), []);
  const artists = useLoad(
    () => artistApi.list({ name, genre, sort, page, size: 12 }),
    [name, genre, sort, page],
  );
  const close = () => setDialog(null);

  return (
    <>
      <header className="page-head">
        <div>
          <p className="eyebrow">Catalog</p>
          <h1>Artists</h1>
          <p>Find an artist to explore their albums and songs.</p>
        </div>
        {canEditCatalog && (
          <button type="button" className="btn btn-primary" onClick={() => setDialog({ kind: "create" })}>
            <Plus size={18} aria-hidden /> New artist
          </button>
        )}
      </header>

      <div className="toolbar">
        <div className="search">
          <Search size={18} aria-hidden />
          <label htmlFor="artist-search" className="sr-only">
            Search artists
          </label>
          <input
            id="artist-search"
            type="search"
            className="input"
            placeholder="Search by name…"
            value={search}
            onChange={(event) => {
              setSearch(event.target.value);
              setPage(0);
            }}
          />
        </div>
        <div className="field">
          <label className="label" htmlFor="artist-genre">
            Genre
          </label>
          <select
            id="artist-genre"
            className="select"
            value={genre}
            onChange={(event) => {
              setGenre(event.target.value);
              setPage(0);
            }}
          >
            <option value="">All genres</option>
            {genres.data?.content.map((g) => (
              <option key={g.id} value={g.name}>
                {g.name}
              </option>
            ))}
          </select>
        </div>
        <div className="field">
          <label className="label" htmlFor="artist-sort">
            <ArrowUpDown size={12} aria-hidden /> Sort
          </label>
          <select id="artist-sort" className="select" value={sort} onChange={(event) => setSort(event.target.value)}>
            <option value="name,asc">Name A–Z</option>
            <option value="name,desc">Name Z–A</option>
            <option value="id,desc">Newest first</option>
          </select>
        </div>
      </div>

      {artists.error ? (
        <ErrorState message={artists.error} onRetry={artists.reload} />
      ) : artists.loading && !artists.data ? (
        <SkeletonCards />
      ) : !artists.data?.content.length ? (
        <EmptyState title="No artists found" />
      ) : (
        <div className="grid grid-3">
          {artists.data.content.map((artist) => (
            <article key={artist.id} className={`card card-hover fade-up ${styles.card}`}>
              <Link href={`/artists/${artist.id}`} className={`plain ${styles.link}`}>
                <span className={styles.avatar} aria-hidden>
                  {initials(artist.name)}
                </span>
                <span style={{ minWidth: 0 }}>
                  <h3 className={styles.name}>{artist.name}</h3>
                  <p className={`muted small ${styles.bio}`}>{artist.biography || "No biography yet."}</p>
                </span>
              </Link>
              {canEditCatalog && (
                <div className={styles.actions}>
                  <button
                    type="button"
                    className="btn btn-ghost btn-sm"
                    onClick={() => setDialog({ kind: "edit", artist })}
                  >
                    <Pencil size={14} aria-hidden /> Edit
                  </button>
                  <button
                    type="button"
                    className="btn btn-ghost btn-sm"
                    onClick={() => setDialog({ kind: "delete", artist })}
                  >
                    <Trash2 size={14} aria-hidden /> Delete
                  </button>
                </div>
              )}
            </article>
          ))}
        </div>
      )}
      <Pagination data={artists.data} onPage={setPage} />

      <Modal
        open={dialog?.kind === "create" || dialog?.kind === "edit"}
        onClose={close}
        title={dialog?.kind === "edit" ? "Edit artist" : "New artist"}
        icon={<Mic2 size={20} />}
      >
        {(dialog?.kind === "create" || dialog?.kind === "edit") && (
          <ArtistForm
            artist={dialog.kind === "edit" ? dialog.artist : undefined}
            onCancel={close}
            onDone={() => {
              close();
              artists.reload();
            }}
          />
        )}
      </Modal>

      <ConfirmDialog
        open={dialog?.kind === "delete"}
        onClose={close}
        title="Delete artist?"
        message={
          dialog?.kind === "delete" && (
            <>
              <strong>{dialog.artist.name}</strong> and all of their albums and songs will be deleted.
            </>
          )
        }
        successMessage="Artist deleted"
        onConfirm={() => (dialog?.kind === "delete" ? artistApi.remove(dialog.artist.id) : Promise.resolve())}
        onDone={artists.reload}
      />
    </>
  );
}
