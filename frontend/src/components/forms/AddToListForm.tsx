"use client";

import Link from "next/link";
import { useState } from "react";
import Field from "../Field";
import FormActions from "./FormActions";
import Spinner from "../Spinner";
import { useLoad } from "@/hooks/useLoad";
import { useSubmit } from "@/hooks/useSubmit";
import { listApi } from "@/lib/endpoints";

/**
 * Adds an album to one or more of the user's lists (checkboxes),
 * optionally at a chosen position (number spinner).
 */
export default function AddToListForm({
  userId,
  albumId,
  onDone,
  onCancel,
}: {
  userId: number;
  albumId: number;
  onDone: () => void;
  onCancel: () => void;
}) {
  const lists = useLoad(() => listApi.byUser(userId, { size: 100 }), [userId]);
  const [selected, setSelected] = useState<number[]>([]);
  const [position, setPosition] = useState("");
  const { busy, errors, formError, submit } = useSubmit();

  const toggle = (id: number) =>
    setSelected((list) => (list.includes(id) ? list.filter((item) => item !== id) : [...list, id]));

  if (lists.loading) return <Spinner label="Loading your lists…" />;

  const available = lists.data?.content ?? [];
  if (!available.length) {
    return (
      <div className="stack">
        <p className="muted">You don&apos;t have any lists yet.</p>
        <Link href="/lists" className="btn btn-primary">
          Create a list
        </Link>
      </div>
    );
  }

  return (
    <form
      className="form"
      onSubmit={(event) => {
        event.preventDefault();
        if (!selected.length) return;
        submit(
          () =>
            Promise.all(
              selected.map((id) => listApi.addAlbum(id, albumId, position ? Number(position) : undefined)),
            ),
          {
            success: `Added to ${selected.length} list${selected.length > 1 ? "s" : ""}`,
            onSuccess: onDone,
          },
        );
      }}
    >
      <fieldset className="choice-group" style={{ flexDirection: "column" }}>
        <legend className="label" style={{ marginBottom: 8 }}>
          Choose lists
        </legend>
        {available.map((list) => {
          const already = list.albums.some((item) => item.albumId === albumId);
          return (
            <label key={list.id} className="choice" style={already ? { opacity: 0.6 } : undefined}>
              <input
                type="checkbox"
                checked={selected.includes(list.id)}
                disabled={already}
                onChange={() => toggle(list.id)}
              />
              <span>
                <strong>{list.name}</strong>
                <span className="muted small">
                  {" "}
                  · {list.albums.length} albums{already ? " · already added" : ""}
                </span>
              </span>
            </label>
          );
        })}
      </fieldset>
      <Field
        label="Position in list"
        htmlFor="list-position"
        error={errors.position}
        hint="Optional — leave empty to add at the end"
      >
        <input
          id="list-position"
          type="number"
          min={1}
          className="input"
          value={position}
          onChange={(event) => setPosition(event.target.value)}
          style={{ maxWidth: 140 }}
        />
      </Field>
      <FormActions busy={busy}
        disabled={!selected.length} onCancel={onCancel} formError={formError} submitLabel="Add" />
    </form>
  );
}
