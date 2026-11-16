"use client";

import Link from "next/link";
import { useState } from "react";
import { Pencil, Plus, Search, Tags, Trash2 } from "lucide-react";
import ConfirmDialog from "@/components/ConfirmDialog";
import Modal from "@/components/Modal";
import Pagination from "@/components/Pagination";
import { SkeletonCards } from "@/components/Skeletons";
import { EmptyState, ErrorState } from "@/components/States";
import GenreForm from "@/components/forms/GenreForm";
import { useAuth } from "@/context/AuthContext";
import { useDebounced } from "@/hooks/useDebounced";
import { useLoad } from "@/hooks/useLoad";
import { genreApi } from "@/lib/endpoints";
import type { GenreResponse } from "@/types/api";

type Dialog = { kind: "create" } | { kind: "edit" | "delete"; genre: GenreResponse } | null;

export default function GenresPage() {
  const { canEditCatalog } = useAuth();
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const [dialog, setDialog] = useState<Dialog>(null);
  const name = useDebounced(search);

  const genres = useLoad(() => genreApi.list({ name, page, size: 24, sort: "name" }), [name, page]);
  const close = () => setDialog(null);

  return (
    <>
      <header className="page-head">
        <div>
          <p className="eyebrow">Catalog</p>
          <h1>Genres</h1>
          <p>Browse artists, albums and songs by style.</p>
        </div>
        {canEditCatalog && (
          <button type="button" className="btn btn-primary" onClick={() => setDialog({ kind: "create" })}>
            <Plus size={18} aria-hidden /> New genre
          </button>
        )}
      </header>

      <div className="toolbar">
        <div className="search">
          <Search size={18} aria-hidden />
          <label htmlFor="genre-search" className="sr-only">
            Search genres
          </label>
          <input
            id="genre-search"
            type="search"
            className="input"
            placeholder="Search genres…"
            value={search}
            onChange={(event) => {
              setSearch(event.target.value);
              setPage(0);
            }}
          />
        </div>
      </div>

      {genres.error ? (
        <ErrorState message={genres.error} onRetry={genres.reload} />
      ) : genres.loading && !genres.data ? (
        <SkeletonCards />
      ) : !genres.data?.content.length ? (
        <EmptyState title="No genres found" />
      ) : (
        <div className="grid">
          {genres.data.content.map((genre) => (
            <article key={genre.id} className="card card-hover fade-up row" style={{ padding: 16 }}>
              <span className="chip" style={{ padding: 8 }}>
                <Tags size={18} aria-hidden />
              </span>
              <Link href={`/genres/${genre.id}`} className="plain" style={{ flex: 1, minWidth: 0 }}>
                <h3>{genre.name}</h3>
              </Link>
              {canEditCatalog && (
                <span className="row" style={{ gap: 2 }}>
                  <button
                    type="button"
                    className="btn btn-ghost btn-icon btn-sm"
                    onClick={() => setDialog({ kind: "edit", genre })}
                    aria-label={`Edit ${genre.name}`}
                  >
                    <Pencil size={16} />
                  </button>
                  <button
                    type="button"
                    className="btn btn-ghost btn-icon btn-sm"
                    onClick={() => setDialog({ kind: "delete", genre })}
                    aria-label={`Delete ${genre.name}`}
                  >
                    <Trash2 size={16} />
                  </button>
                </span>
              )}
            </article>
          ))}
        </div>
      )}
      <Pagination data={genres.data} onPage={setPage} />

      <Modal
        open={dialog?.kind === "create" || dialog?.kind === "edit"}
        onClose={close}
        title={dialog?.kind === "edit" ? "Edit genre" : "New genre"}
        icon={<Tags size={20} />}
        size="sm"
      >
        {(dialog?.kind === "create" || dialog?.kind === "edit") && (
          <GenreForm
            genre={dialog.kind === "edit" ? dialog.genre : undefined}
            onCancel={close}
            onDone={() => {
              close();
              genres.reload();
            }}
          />
        )}
      </Modal>

      <ConfirmDialog
        open={dialog?.kind === "delete"}
        onClose={close}
        title="Delete genre?"
        message={
          dialog?.kind === "delete" && (
            <>
              <strong>{dialog.genre.name}</strong> will be removed from all artists, albums and songs.
            </>
          )
        }
        successMessage="Genre deleted"
        onConfirm={() => (dialog?.kind === "delete" ? genreApi.remove(dialog.genre.id) : Promise.resolve())}
        onDone={genres.reload}
      />
    </>
  );
}
