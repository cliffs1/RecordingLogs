import { ApiError } from "./api";

export function errorMessage(error: unknown): string {
  if (error instanceof ApiError) return error.message;
  if (error instanceof Error) return error.message;
  return "Something went wrong";
}

/**
 * Backend validation errors arrive as one string: "field: message, field: message".
 * Split them into a per-field map so forms can show them next to the inputs.
 */
export function fieldErrors(error: unknown): Record<string, string> {
  if (!(error instanceof ApiError) || error.status !== 400) return {};
  const result: Record<string, string> = {};
  for (const part of error.message.split(/,\s*(?=\w+:)/)) {
    const match = part.match(/^(\w+):\s*(.+)$/);
    if (match) result[match[1]] = match[2];
  }
  return result;
}

export function formatDuration(seconds: number | null | undefined): string {
  if (seconds == null) return "–";
  const m = Math.floor(seconds / 60);
  const s = seconds % 60;
  return `${m}:${String(s).padStart(2, "0")}`;
}

export function formatDate(value: string | null | undefined): string {
  if (!value) return "–";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleDateString("en-GB", { year: "numeric", month: "short", day: "numeric" });
}

export function formatYear(value: string | null | undefined): string {
  return value ? value.slice(0, 4) : "";
}

export function timeAgo(value: string): string {
  const diff = (Date.now() - new Date(value).getTime()) / 1000;
  if (diff < 60) return "just now";
  if (diff < 3600) return `${Math.floor(diff / 60)} min ago`;
  if (diff < 86400) return `${Math.floor(diff / 3600)} h ago`;
  if (diff < 86400 * 30) return `${Math.floor(diff / 86400)} d ago`;
  return formatDate(value);
}

export function initials(name: string): string {
  return name
    .split(/[\s_.-]+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]!.toUpperCase())
    .join("");
}

export function albumHref(artistId: number, albumId: number) {
  return `/artists/${artistId}/albums/${albumId}`;
}
