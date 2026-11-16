import { ChevronLeft, ChevronRight } from "lucide-react";
import type { Page } from "@/types/api";

/** Prev/next pager driven by the PageResponse metadata from the backend. */
export default function Pagination({
  data,
  onPage,
}: {
  data: Page<unknown> | undefined;
  onPage: (page: number) => void;
}) {
  if (!data || data.totalPages <= 1) return null;
  const { page, totalPages, totalElements } = data;

  return (
    <nav className="row" aria-label="Pagination" style={{ justifyContent: "center", marginTop: 32 }}>
      <button
        type="button"
        className="btn btn-secondary btn-sm"
        onClick={() => onPage(page - 1)}
        disabled={page <= 0}
      >
        <ChevronLeft size={16} aria-hidden /> Previous
      </button>
      <span className="muted small">
        Page {page + 1} of {totalPages} · {totalElements} items
      </span>
      <button
        type="button"
        className="btn btn-secondary btn-sm"
        onClick={() => onPage(page + 1)}
        disabled={page >= totalPages - 1}
      >
        Next <ChevronRight size={16} aria-hidden />
      </button>
    </nav>
  );
}
