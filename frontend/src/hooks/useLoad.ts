"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { errorMessage } from "@/lib/format";

interface LoadState<T> {
  data: T | undefined;
  error: string | null;
  loading: boolean;
  reload: () => void;
  setData: (data: T | undefined) => void;
}

interface Result<T> {
  key: string | null;
  data: T | undefined;
  error: string | null;
}

/**
 * Runs an async loader whenever `deps` change and tracks loading/error state.
 * The previous data stays available while a new request is in flight, and
 * responses for outdated deps are ignored. Pass `enabled = false` to skip loading.
 */
export function useLoad<T>(
  loader: () => Promise<T>,
  deps: React.DependencyList,
  enabled = true,
): LoadState<T> {
  const [tick, setTick] = useState(0);
  const [result, setResult] = useState<Result<T>>({ key: null, data: undefined, error: null });
  const loaderRef = useRef(loader);

  useEffect(() => {
    loaderRef.current = loader;
  });

  // One key per request: deps + manual reloads. `loading` = no result for this key yet.
  const key = JSON.stringify([...deps, tick]);

  useEffect(() => {
    if (!enabled) return;
    let active = true;
    loaderRef.current().then(
      (data) => {
        if (active) setResult({ key, data, error: null });
      },
      (error) => {
        if (active) setResult((previous) => ({ key, data: previous.data, error: errorMessage(error) }));
      },
    );
    return () => {
      active = false;
    };
  }, [key, enabled]);

  const reload = useCallback(() => setTick((value) => value + 1), []);
  const setData = useCallback(
    (data: T | undefined) => setResult((previous) => ({ ...previous, data })),
    [],
  );

  const loading = enabled && result.key !== key;
  return {
    data: result.data,
    error: loading ? null : result.error,
    loading,
    reload,
    setData,
  };
}
