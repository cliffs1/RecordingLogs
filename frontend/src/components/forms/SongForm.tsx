"use client";

import { useState } from "react";
import Field from "../Field";
import FormActions from "./FormActions";
import { useSubmit } from "@/hooks/useSubmit";
import { songApi } from "@/lib/endpoints";
import type { SongResponse } from "@/types/api";

export default function SongForm({
  artistId,
  albumId,
  song,
  nextTrack = 1,
  onDone,
  onCancel,
}: {
  artistId: number;
  albumId: number;
  song?: SongResponse;
  nextTrack?: number;
  onDone: (song: SongResponse) => void;
  onCancel: () => void;
}) {
  const [title, setTitle] = useState(song?.title ?? "");
  const [trackNumber, setTrackNumber] = useState(String(song?.trackNumber ?? nextTrack));
  const [discNumber, setDiscNumber] = useState(String(song?.discNumber ?? 1));
  const [minutes, setMinutes] = useState(
    song?.durationSeconds != null ? String(Math.floor(song.durationSeconds / 60)) : "",
  );
  const [seconds, setSeconds] = useState(
    song?.durationSeconds != null ? String(song.durationSeconds % 60) : "",
  );
  const [releaseDate, setReleaseDate] = useState(song?.originalReleaseDate ?? "");
  const { busy, errors, formError, submit } = useSubmit();

  return (
    <form
      className="form"
      onSubmit={(event) => {
        event.preventDefault();
        const hasDuration = minutes !== "" || seconds !== "";
        const body = {
          title: title.trim(),
          trackNumber: Number(trackNumber),
          discNumber: discNumber ? Number(discNumber) : null,
          durationSeconds: hasDuration ? Number(minutes || 0) * 60 + Number(seconds || 0) : null,
          originalReleaseDate: releaseDate || null,
        };
        submit(
          () =>
            song
              ? songApi.update(artistId, albumId, song.id, body)
              : songApi.create(artistId, albumId, body),
          { success: song ? "Song updated" : "Song added", onSuccess: onDone },
        );
      }}
    >
      <Field label="Title" htmlFor="song-title" required error={errors.title}>
        <input
          id="song-title"
          className="input"
          value={title}
          onChange={(event) => setTitle(event.target.value)}
          required
          autoFocus
        />
      </Field>
      <div className="form-row">
        <Field label="Track number" htmlFor="song-track" required error={errors.trackNumber}>
          <input
            id="song-track"
            type="number"
            className="input"
            min={1}
            step={1}
            value={trackNumber}
            onChange={(event) => setTrackNumber(event.target.value)}
            required
          />
        </Field>
        <Field label="Disc number" htmlFor="song-disc" error={errors.discNumber}>
          <input
            id="song-disc"
            type="number"
            className="input"
            min={1}
            step={1}
            value={discNumber}
            onChange={(event) => setDiscNumber(event.target.value)}
          />
        </Field>
      </div>
      <fieldset className="field" style={{ border: 0, padding: 0, margin: 0 }}>
        <legend className="label" style={{ marginBottom: 4 }}>
          Duration (min : sec)
        </legend>
        <div className="row" style={{ flexWrap: "nowrap" }}>
          <label htmlFor="song-min" className="sr-only">
            Minutes
          </label>
          <input
            id="song-min"
            type="number"
            className="input"
            min={0}
            max={600}
            value={minutes}
            onChange={(event) => setMinutes(event.target.value)}
            placeholder="3"
          />
          <span aria-hidden>:</span>
          <label htmlFor="song-sec" className="sr-only">
            Seconds
          </label>
          <input
            id="song-sec"
            type="number"
            className="input"
            min={0}
            max={59}
            value={seconds}
            onChange={(event) => setSeconds(event.target.value)}
            placeholder="45"
          />
        </div>
        {errors.durationSeconds && <span className="error-text">{errors.durationSeconds}</span>}
      </fieldset>
      <Field label="Original release date" htmlFor="song-date" error={errors.originalReleaseDate}>
        <input
          id="song-date"
          type="date"
          className="input"
          value={releaseDate}
          onChange={(event) => setReleaseDate(event.target.value)}
        />
      </Field>
      <FormActions busy={busy} onCancel={onCancel} formError={formError} submitLabel={song ? "Save" : "Add song"} />
    </form>
  );
}
