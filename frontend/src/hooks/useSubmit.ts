"use client";

import { useState } from "react";
import { useToast } from "@/components/Toast";
import { errorMessage, fieldErrors } from "@/lib/format";

/**
 * Shared submit handling for forms: busy flag, per-field errors from backend
 * validation ("field: message"), a general error banner and a success toast.
 */
export function useSubmit() {
  const toast = useToast();
  const [busy, setBusy] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);

  async function submit<T>(
    action: () => Promise<T>,
    options: { success?: string; onSuccess?: (result: T) => void } = {},
  ) {
    setBusy(true);
    setErrors({});
    setFormError(null);
    try {
      const result = await action();
      if (options.success) toast.success(options.success);
      options.onSuccess?.(result);
      return result;
    } catch (error) {
      const fields = fieldErrors(error);
      if (Object.keys(fields).length) setErrors(fields);
      else setFormError(errorMessage(error));
      return undefined;
    } finally {
      setBusy(false);
    }
  }

  return { busy, errors, setErrors, formError, submit };
}
