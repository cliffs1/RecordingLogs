"use client";

import { useState } from "react";
import Field from "../Field";
import FormActions from "./FormActions";
import { useSubmit } from "@/hooks/useSubmit";
import { reviewApi } from "@/lib/endpoints";
import type { AlbumReviewResponse } from "@/types/api";

const MAX = 10000;

export default function ReviewForm({
  albumId,
  review,
  onDone,
  onCancel,
}: {
  albumId: number;
  review?: AlbumReviewResponse;
  onDone: (review: AlbumReviewResponse) => void;
  onCancel: () => void;
}) {
  const [title, setTitle] = useState(review?.title ?? "");
  const [content, setContent] = useState(review?.content ?? "");
  const { busy, errors, formError, submit } = useSubmit();

  return (
    <form
      className="form"
      onSubmit={(event) => {
        event.preventDefault();
        const body = { title: title.trim(), content: content.trim() };
        submit(() => (review ? reviewApi.update(review.id, body) : reviewApi.create(albumId, body)), {
          success: review ? "Review updated" : "Review published",
          onSuccess: onDone,
        });
      }}
    >
      <Field label="Headline" htmlFor="review-title" required error={errors.title}>
        <input
          id="review-title"
          className="input"
          value={title}
          onChange={(event) => setTitle(event.target.value)}
          required
          maxLength={200}
          autoFocus
          placeholder="Sum up the album in one line"
        />
      </Field>
      <Field
        label="Review"
        htmlFor="review-content"
        required
        error={errors.content}
        hint={`${content.length} / ${MAX} characters`}
      >
        <textarea
          id="review-content"
          className="textarea"
          value={content}
          onChange={(event) => setContent(event.target.value)}
          required
          maxLength={MAX}
          rows={8}
          placeholder="What did you like? What didn't work?"
        />
      </Field>
      <FormActions busy={busy} onCancel={onCancel} formError={formError} submitLabel={review ? "Save" : "Publish"} />
    </form>
  );
}
