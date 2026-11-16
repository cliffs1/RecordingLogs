import { AlertTriangle, Inbox, RotateCw } from "lucide-react";

export function EmptyState({
  title,
  children,
  icon,
}: {
  title: string;
  children?: React.ReactNode;
  icon?: React.ReactNode;
}) {
  return (
    <div className="empty">
      {icon ?? <Inbox size={32} aria-hidden />}
      <strong>{title}</strong>
      {children}
    </div>
  );
}

export function ErrorState({ message, onRetry }: { message: string; onRetry?: () => void }) {
  return (
    <div className="empty" role="alert" style={{ borderColor: "var(--rose-50)" }}>
      <AlertTriangle size={32} color="var(--rose-600)" aria-hidden />
      <strong>Could not load data</strong>
      <span>{message}</span>
      {onRetry && (
        <button type="button" className="btn btn-secondary btn-sm" onClick={onRetry}>
          <RotateCw size={14} aria-hidden /> Try again
        </button>
      )}
    </div>
  );
}
