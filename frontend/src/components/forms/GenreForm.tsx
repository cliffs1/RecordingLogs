"use client";

import { useState } from "react";
import Field from "../Field";
import FormActions from "./FormActions";
import { useSubmit } from "@/hooks/useSubmit";
import { genreApi } from "@/lib/endpoints";
import type { GenreResponse } from "@/types/api";

export default function GenreForm({
  genre,
  onDone,
  onCancel,
}: {
  genre?: GenreResponse;
  onDone: (genre: GenreResponse) => void;
  onCancel: () => void;
}) {
  const [name, setName] = useState(genre?.name ?? "");
  const { busy, errors, formError, submit } = useSubmit();

  return (
    <form
      className="form"
      onSubmit={(event) => {
        event.preventDefault();
        const body = { name: name.trim() };
        submit(() => (genre ? genreApi.update(genre.id, body) : genreApi.create(body)), {
          success: genre ? "Genre updated" : "Genre created",
          onSuccess: onDone,
        });
      }}
    >
      <Field label="Name" htmlFor="genre-name" required error={errors.name}>
        <input
          id="genre-name"
          className="input"
          value={name}
          onChange={(event) => setName(event.target.value)}
          required
          maxLength={100}
          autoFocus
          placeholder="e.g. Shoegaze"
        />
      </Field>
      <FormActions busy={busy} onCancel={onCancel} formError={formError} submitLabel={genre ? "Save" : "Create"} />
    </form>
  );
}
