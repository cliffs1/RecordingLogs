"use client";

import Link from "next/link";
import { BookOpen, Pencil, Trash2 } from "lucide-react";
import Cover from "./Cover";
import { albumHref, formatDate, timeAgo } from "@/lib/format";
import type { AlbumReviewResponse } from "@/types/api";
import styles from "./ReviewCard.module.css";

export default function ReviewCard({
  review,
  showAlbum = true,
  canModify,
  onOpen,
  onEdit,
  onDelete,
}: {
  review: AlbumReviewResponse;
  showAlbum?: boolean;
  canModify?: boolean;
  onOpen: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
}) {
  return (
    <article className={`card card-hover fade-up ${styles.card}`}>
      {showAlbum && (
        <Link href={albumHref(review.artistId, review.albumId)} className={`plain ${styles.cover}`}>
          <Cover src={review.coverUrl} alt={`Cover of ${review.albumTitle}`} />
        </Link>
      )}
      <div className={styles.body}>
        {showAlbum && (
          <p className="small muted">
            <Link href={albumHref(review.artistId, review.albumId)}>{review.albumTitle}</Link> ·{" "}
            {review.artistName}
          </p>
        )}
        <h3 className={styles.title}>{review.title}</h3>
        <p className={styles.excerpt}>{review.content}</p>
        <div className={styles.meta}>
          <span className="small muted">
            by <Link href={`/users/${review.userId}`}>{review.username}</Link> ·{" "}
            <time dateTime={review.createdAt} title={formatDate(review.createdAt)}>
              {timeAgo(review.createdAt)}
            </time>
          </span>
          <span className="spacer" />
          <button type="button" className="btn btn-ghost btn-sm" onClick={onOpen}>
            <BookOpen size={14} aria-hidden /> Read
          </button>
          {canModify && (
            <>
              <button type="button" className="btn btn-ghost btn-sm btn-icon" onClick={onEdit} aria-label="Edit review">
                <Pencil size={14} />
              </button>
              <button type="button" className="btn btn-ghost btn-sm btn-icon" onClick={onDelete} aria-label="Delete review">
                <Trash2 size={14} />
              </button>
            </>
          )}
        </div>
      </div>
    </article>
  );
}
