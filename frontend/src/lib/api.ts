import type { TokenResponse } from "@/types/api";

/**
 * Thin fetch wrapper around the RecordingLogs REST API.
 *
 * - The access token is kept only in memory (never localStorage).
 * - The refresh token is an HttpOnly cookie set by the backend on /api/auth.
 * - When a request returns 401, the wrapper calls /api/auth/refresh once,
 *   stores the new access token and retries the original request.
 */

export class ApiError extends Error {
  status: number;
  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

let accessToken: string | null = null;
let refreshPromise: Promise<TokenResponse | null> | null = null;
const listeners = new Set<(session: TokenResponse | null) => void>();

export function setAccessToken(token: string | null) {
  accessToken = token;
}

export function getAccessToken() {
  return accessToken;
}

/** AuthContext subscribes so a silent refresh (or failure) updates the UI. */
export function onSessionChange(listener: (session: TokenResponse | null) => void) {
  listeners.add(listener);
  return () => {
    listeners.delete(listener);
  };
}

function emit(session: TokenResponse | null) {
  listeners.forEach((listener) => listener(session));
}

async function readError(response: Response): Promise<string> {
  try {
    const body = await response.json();
    if (body && typeof body === "object") {
      return body.error ?? body.message ?? response.statusText;
    }
  } catch {
    /* not JSON */
  }
  if (response.status === 401) return "Please log in to continue";
  if (response.status === 403) return "You do not have permission to do this";
  return response.statusText || `Request failed (${response.status})`;
}

/** Exchanges the refresh cookie for a new access token. Concurrent callers share one request. */
export function refreshSession(): Promise<TokenResponse | null> {
  if (!refreshPromise) {
    refreshPromise = (async () => {
      try {
        const response = await fetch("/api/auth/refresh", {
          method: "POST",
          credentials: "include",
        });
        if (!response.ok) {
          setAccessToken(null);
          emit(null);
          return null;
        }
        const session = (await response.json()) as TokenResponse;
        setAccessToken(session.accessToken);
        emit(session);
        return session;
      } catch {
        return null;
      } finally {
        refreshPromise = null;
      }
    })();
  }
  return refreshPromise;
}

type Query = Record<string, string | number | boolean | null | undefined>;

export interface RequestOptions {
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  body?: unknown;
  query?: Query;
  /** Skip the automatic refresh-and-retry on 401 (used by the auth endpoints). */
  noRetry?: boolean;
}

export function buildQuery(query?: Query): string {
  if (!query) return "";
  const params = new URLSearchParams();
  for (const [key, value] of Object.entries(query)) {
    if (value === undefined || value === null || value === "") continue;
    params.set(key, String(value));
  }
  const text = params.toString();
  return text ? `?${text}` : "";
}

export async function api<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = "GET", body, query, noRetry } = options;
  const url = `/api${path}${buildQuery(query)}`;

  const send = () => {
    const headers: Record<string, string> = { Accept: "application/json" };
    if (body !== undefined) headers["Content-Type"] = "application/json";
    if (accessToken) headers.Authorization = `Bearer ${accessToken}`;
    return fetch(url, {
      method,
      headers,
      credentials: "include",
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  };

  let response = await send();

  if (response.status === 401 && !noRetry) {
    const session = await refreshSession();
    if (session) response = await send();
  }

  if (!response.ok) {
    throw new ApiError(response.status, await readError(response));
  }

  if (response.status === 204) return undefined as T;
  const text = await response.text();
  return (text ? JSON.parse(text) : undefined) as T;
}
