"use client";

import Link from "next/link";
import { useState } from "react";
import { Save, Star, Trash2 } from "lucide-react";
import { useLoad } from "@/hooks/useLoad";
import { ApiError } from "@/lib/api";
import { errorMessage } from "@/lib/format";
import { useAuth } from "@/context/AuthContext";
import { useToast } from "./Toast";
import Spinner from "./Spinner";
import styles from "./MyRating.module.css";

interface RatingApi {
  get: (targetId: number, userId: number) => Promise<{ rating: number }>;
  create: (targetId: number, rating: number) => Promise<unknown>;
  update: (targetId: number, userId: number, rating: number) => Promise<unknown>;
  remove: (targetId: number, userId: number) => Promise<unknown>;
}

/**
 * The logged-in user's own 1–10 rating for an album or song.
 * GET one → POST (first time) / PUT (change) / DELETE (remove).
 */
export default function MyRating({
  targetId,
  ratingApi,
  onChange,
  compact,
}: {
  targetId: number;
  ratingApi: RatingApi;
  onChange?: () => void;
  compact?: boolean;
}) {
  const { user } = useAuth();
  const toast = useToast();
  // GET the user's existing rating; 404 means "not rated yet".
  const mine = useLoad(
    () =>
      ratingApi.get(targetId, user!.id).then(
        (response) => response.rating as number | null,
        (error) => {
          if (error instanceof ApiError && error.status === 404) return null;
          throw error;
        },
      ),
    [targetId, user?.id],
    !!user,
  );
  const saved = mine.data ?? null;
  const [draft, setDraft] = useState<number | null>(null);
  const [busy, setBusy] = useState(false);
  const value = draft ?? saved ?? 7;
  const setValue = setDraft;
  const setSaved = (rating: number | null) => {
    mine.setData(rating);
    setDraft(null);
  };
  const loading = mine.loading;

  if (!user) {
    return (
      <p className="muted small">
        <Link href="/login">Log in</Link> to rate.
      </p>
    );
  }

  async function save() {
    if (!user) return;
    setBusy(true);
    try {
      if (saved == null) await ratingApi.create(targetId, value);
      else await ratingApi.update(targetId, user.id, value);
      toast.success(saved == null ? `Rated ${value}/10` : `Rating changed to ${value}/10`);
      setSaved(value);
      onChange?.();
    } catch (error) {
      toast.error(errorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  async function remove() {
    if (!user) return;
    setBusy(true);
    try {
      await ratingApi.remove(targetId, user.id);
      setSaved(null);
      toast.success("Rating removed");
      onChange?.();
    } catch (error) {
      toast.error(errorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  if (loading) return <Spinner size={18} />;

  const id = `rating-${targetId}`;
  return (
    <div className={`${styles.wrap} ${compact ? styles.compact : ""}`}>
      <label htmlFor={id} className={styles.label}>
        <Star size={16} aria-hidden /> {saved == null ? "Your rating" : `Your rating: ${saved}/10`}
      </label>
      <div className={styles.controls}>
        <input
          id={id}
          type="range"
          min={1}
          max={10}
          step={1}
          value={value}
          onChange={(event) => setValue(Number(event.target.value))}
          className={styles.range}
          style={{ "--fill": `${((value - 1) / 9) * 100}%` } as React.CSSProperties}
          aria-valuetext={`${value} out of 10`}
        />
        <output htmlFor={id} className={styles.value}>
          {value}
        </output>
        <button
          type="button"
          className="btn btn-primary btn-sm"
          onClick={save}
          disabled={busy || value === saved}
        >
          {busy ? <Spinner size={14} /> : <Save size={14} aria-hidden />}
          {saved == null ? "Rate" : "Update"}
        </button>
        {saved != null && (
          <button
            type="button"
            className="btn btn-ghost btn-sm btn-icon"
            onClick={remove}
            disabled={busy}
            aria-label="Remove rating"
            title="Remove rating"
          >
            <Trash2 size={14} />
          </button>
        )}
      </div>
    </div>
  );
}
