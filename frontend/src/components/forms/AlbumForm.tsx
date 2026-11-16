"use client";

import { useState } from "react";
import Field from "../Field";
import Cover from "../Cover";
import FormActions from "./FormActions";
import { useSubmit } from "@/hooks/useSubmit";
import { albumApi } from "@/lib/endpoints";
import type { AlbumResponse } from "@/types/api";

export default function AlbumForm({
  artistId,
  album,
  onDone,
  onCancel,
}: {
  artistId: number;
  album?: AlbumResponse;
  onDone: (album: AlbumResponse) => void;
  onCancel: () => void;
}) {
  const [title, setTitle] = useState(album?.title ?? "");
  const [releaseDate, setReleaseDate] = useState(album?.releaseDate ?? "");
  const [coverUrl, setCoverUrl] = useState(album?.coverUrl ?? "");
  const { busy, errors, formError, submit } = useSubmit();

  const today = new Date().toISOString().slice(0, 10);

  return (
    <form
      className="form"
      onSubmit={(event) => {
        event.preventDefault();
        const body = {
          title: title.trim(),
          releaseDate: releaseDate || null,
          coverUrl: coverUrl.trim() || null,
        };
        submit(
          () => (album ? albumApi.update(artistId, album.id, body) : albumApi.create(artistId, body)),
          { success: album ? "Album updated" : "Album created", onSuccess: onDone },
        );
      }}
    >
      <div className="form-row" style={{ gridTemplateColumns: "1fr 140px", alignItems: "start" }}>
        <div className="stack">
          <Field label="Title" htmlFor="album-title" required error={errors.title}>
            <input
              id="album-title"
              className="input"
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              required
              autoFocus
              placeholder="e.g. OK Computer"
            />
          </Field>
          <Field label="Release date" htmlFor="album-date" error={errors.releaseDate}>
            <input
              id="album-date"
              type="date"
              className="input"
              value={releaseDate}
              max={today}
              onChange={(event) => setReleaseDate(event.target.value)}
            />
          </Field>
        </div>
        <div className="field">
          <span className="label">Preview</span>
          <Cover src={coverUrl || null} alt="Cover preview" />
        </div>
      </div>
      <Field
        label="Cover image URL"
        htmlFor="album-cover"
        error={errors.coverUrl}
        hint="Paste a link to a square image (https://…)"
      >
        <input
          id="album-cover"
          type="url"
          className="input"
          value={coverUrl}
          onChange={(event) => setCoverUrl(event.target.value)}
          placeholder="https://…"
        />
      </Field>
      <FormActions busy={busy} onCancel={onCancel} formError={formError} submitLabel={album ? "Save" : "Create"} />
    </form>
  );
}
