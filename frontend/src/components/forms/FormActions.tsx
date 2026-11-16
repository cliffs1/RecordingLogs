import { Check } from "lucide-react";
import Spinner from "../Spinner";

export default function FormActions({
  busy,
  disabled,
  onCancel,
  submitLabel = "Save",
  formError,
}: {
  busy: boolean;
  disabled?: boolean;
  onCancel?: () => void;
  submitLabel?: string;
  formError?: string | null;
}) {
  return (
    <>
      {formError && (
        <div className="form-error" role="alert">
          {formError}
        </div>
      )}
      <div className="form-actions">
        {onCancel && (
          <button type="button" className="btn btn-ghost" onClick={onCancel} disabled={busy}>
            Cancel
          </button>
        )}
        <button type="submit" className="btn btn-primary" disabled={busy || disabled}>
          {busy ? <Spinner size={16} /> : <Check size={16} aria-hidden />}
          {submitLabel}
        </button>
      </div>
    </>
  );
}
