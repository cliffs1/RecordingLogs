"use client";

import Link from "next/link";
import { useState } from "react";
import { Plus, Tag, X } from "lucide-react";
import { useLoad } from "@/hooks/useLoad";
import { genreApi } from "@/lib/endpoints";
import { errorMessage } from "@/lib/format";
import { useToast } from "./Toast";
import Spinner from "./Spinner";
import type { GenreResponse, Page } from "@/types/api";

interface GenreChipsProps {
  /** Loads the genres attached to the entity. */
  load: () => Promise<Page<GenreResponse>>;
  add: (genreId: number) => Promise<unknown>;
  remove: (genreId: number) => Promise<unknown>;
  canEdit: boolean;
  deps: React.DependencyList;
}

/** Genres attached to an artist, album or song, with attach (select) and detach (×). */
export default function GenreChips({ load, add, remove, canEdit, deps }: GenreChipsProps) {
  const toast = useToast();
  const attached = useLoad(() => load(), deps);
  const all = useLoad(() => genreApi.list({ size: 100, sort: "name" }), [], canEdit);
  const [selected, setSelected] = useState("");
  const [busy, setBusy] = useState(false);

  const attachedIds = new Set(attached.data?.content.map((genre) => genre.id));
  const available = all.data?.content.filter((genre) => !attachedIds.has(genre.id)) ?? [];

  async function attach() {
    if (!selected) return;
    setBusy(true);
    try {
      await add(Number(selected));
      setSelected("");
      attached.reload();
      toast.success("Genre added");
    } catch (error) {
      toast.error(errorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  async function detach(genre: GenreResponse) {
    try {
      await remove(genre.id);
      attached.reload();
      toast.success(`Removed “${genre.name}”`);
    } catch (error) {
      toast.error(errorMessage(error));
    }
  }

  return (
    <div className="row" style={{ gap: 8 }}>
      {attached.loading && !attached.data ? (
        <Spinner size={16} />
      ) : attached.data?.content.length ? (
        attached.data.content.map((genre) => (
          <span key={genre.id} className="chip">
            <Tag size={12} aria-hidden />
            <Link href={`/genres/${genre.id}`} className="plain">
              {genre.name}
            </Link>
            {canEdit && (
              <button type="button" onClick={() => detach(genre)} aria-label={`Remove ${genre.name}`}>
                <X size={14} />
              </button>
            )}
          </span>
        ))
      ) : (
        <span className="muted small">No genres</span>
      )}

      {canEdit && (
        <span className="row" style={{ gap: 4 }}>
          <label htmlFor="genre-attach" className="sr-only">
            Add genre
          </label>
          <select
            id="genre-attach"
            className="select"
            style={{ minHeight: 32, width: "auto", padding: "2px 8px", fontSize: 14 }}
            value={selected}
            onChange={(event) => setSelected(event.target.value)}
          >
            <option value="">Add genre…</option>
            {available.map((genre) => (
              <option key={genre.id} value={genre.id}>
                {genre.name}
              </option>
            ))}
          </select>
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            onClick={attach}
            disabled={!selected || busy}
            aria-label="Add selected genre"
          >
            {busy ? <Spinner size={14} /> : <Plus size={14} />}
          </button>
        </span>
      )}
    </div>
  );
}
