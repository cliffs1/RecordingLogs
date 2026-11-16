"use client";

import { useState } from "react";
import Field from "../Field";
import FormActions from "./FormActions";
import { useSubmit } from "@/hooks/useSubmit";
import { listApi } from "@/lib/endpoints";
import type { AlbumListResponse } from "@/types/api";

export default function ListForm({
  list,
  onDone,
  onCancel,
}: {
  list?: AlbumListResponse;
  onDone: (list: AlbumListResponse) => void;
  onCancel: () => void;
}) {
  const [name, setName] = useState(list?.name ?? "");
  const [description, setDescription] = useState(list?.description ?? "");
  const { busy, errors, formError, submit } = useSubmit();

  return (
    <form
      className="form"
      onSubmit={(event) => {
        event.preventDefault();
        const body = { name: name.trim(), description: description.trim() || undefined };
        submit(() => (list ? listApi.update(list.id, body) : listApi.create(body)), {
          success: list ? "List updated" : "List created",
          onSuccess: onDone,
        });
      }}
    >
      <Field label="List name" htmlFor="list-name" required error={errors.name}>
        <input
          id="list-name"
          className="input"
          value={name}
          onChange={(event) => setName(event.target.value)}
          required
          maxLength={100}
          autoFocus
          placeholder="e.g. Best of the 90s"
        />
      </Field>
      <Field
        label="Description"
        htmlFor="list-desc"
        error={errors.description}
        hint={`${description.length} / 2000 characters`}
      >
        <textarea
          id="list-desc"
          className="textarea"
          value={description}
          onChange={(event) => setDescription(event.target.value)}
          maxLength={2000}
          rows={4}
        />
      </Field>
      <FormActions busy={busy} onCancel={onCancel} formError={formError} submitLabel={list ? "Save" : "Create"} />
    </form>
  );
}
