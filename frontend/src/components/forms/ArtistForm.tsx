"use client";

import { useState } from "react";
import Field from "../Field";
import FormActions from "./FormActions";
import { useSubmit } from "@/hooks/useSubmit";
import { artistApi } from "@/lib/endpoints";
import type { ArtistResponse } from "@/types/api";

export default function ArtistForm({
  artist,
  onDone,
  onCancel,
}: {
  artist?: ArtistResponse;
  onDone: (artist: ArtistResponse) => void;
  onCancel: () => void;
}) {
  const [name, setName] = useState(artist?.name ?? "");
  const [biography, setBiography] = useState(artist?.biography ?? "");
  const { busy, errors, formError, submit } = useSubmit();

  return (
    <form
      className="form"
      onSubmit={(event) => {
        event.preventDefault();
        const body = { name: name.trim(), biography: biography.trim() || undefined };
        submit(() => (artist ? artistApi.update(artist.id, body) : artistApi.create(body)), {
          success: artist ? "Artist updated" : "Artist created",
          onSuccess: onDone,
        });
      }}
    >
      <Field label="Name" htmlFor="artist-name" required error={errors.name}>
        <input
          id="artist-name"
          className="input"
          value={name}
          onChange={(event) => setName(event.target.value)}
          required
          autoFocus
          placeholder="e.g. Radiohead"
        />
      </Field>
      <Field
        label="Biography"
        htmlFor="artist-bio"
        error={errors.biography}
        hint={`${biography.length} characters`}
      >
        <textarea
          id="artist-bio"
          className="textarea"
          value={biography}
          onChange={(event) => setBiography(event.target.value)}
          rows={5}
          placeholder="A short introduction to the artist…"
        />
      </Field>
      <FormActions busy={busy} onCancel={onCancel} formError={formError} submitLabel={artist ? "Save" : "Create"} />
    </form>
  );
}
