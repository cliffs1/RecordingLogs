"use client";

import Link from "next/link";
import { MessageSquareText } from "lucide-react";
import Modal from "./Modal";
import Cover from "./Cover";
import Spinner from "./Spinner";
import { ErrorState } from "./States";
import { useLoad } from "@/hooks/useLoad";
import { reviewApi } from "@/lib/endpoints";
import { albumHref, formatDate } from "@/lib/format";
import styles from "./ReviewCard.module.css";

/** Full review, loaded with GET /api/reviews/{id}. */
export default function ReviewModal({ reviewId, onClose }: { reviewId: number | null; onClose: () => void }) {
  const review = useLoad(() => reviewApi.get(reviewId!), [reviewId], reviewId != null);
  const r = review.data?.id === reviewId ? review.data : undefined;

  return (
    <Modal
      open={reviewId != null}
      onClose={onClose}
      title={r?.title ?? "Review"}
      description={r ? `${r.albumTitle} · ${r.artistName}` : undefined}
      icon={<MessageSquareText size={20} />}
      size="lg"
    >
      {review.error ? (
        <ErrorState message={review.error} onRetry={review.reload} />
      ) : !r ? (
        <Spinner label="Loading review…" />
      ) : (
        <div className="row" style={{ alignItems: "flex-start", gap: 24 }}>
          <Link href={albumHref(r.artistId, r.albumId)} className="plain" style={{ width: 160, maxWidth: "35%" }} onClick={onClose}>
            <Cover src={r.coverUrl} alt={`Cover of ${r.albumTitle}`} />
          </Link>
          <div className="stack" style={{ flex: 1, minWidth: 220 }}>
            <p className="small muted">
              by{" "}
              <Link href={`/users/${r.userId}`} onClick={onClose}>
                {r.username}
              </Link>{" "}
              · {formatDate(r.createdAt)}
              {r.updatedAt !== r.createdAt && ` · edited ${formatDate(r.updatedAt)}`}
            </p>
            <p className={styles.full}>{r.content}</p>
          </div>
        </div>
      )}
    </Modal>
  );
}
