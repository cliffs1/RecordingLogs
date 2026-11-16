interface FieldProps {
  label: string;
  htmlFor?: string;
  required?: boolean;
  hint?: string;
  error?: string;
  children: React.ReactNode;
}

/** Label + control + hint/error, so every form looks and behaves the same. */
export default function Field({ label, htmlFor, required, hint, error, children }: FieldProps) {
  return (
    <div className={`field${error ? " field-error" : ""}`}>
      <label className="label" htmlFor={htmlFor}>
        {label}
        {required && (
          <span className="req" aria-hidden>
            {" "}
            *
          </span>
        )}
      </label>
      {children}
      {error ? (
        <span className="error-text" role="alert">
          {error}
        </span>
      ) : (
        hint && <span className="hint">{hint}</span>
      )}
    </div>
  );
}
