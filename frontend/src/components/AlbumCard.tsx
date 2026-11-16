import Link from "next/link";
import Cover from "./Cover";
import { albumHref, formatYear } from "@/lib/format";
import styles from "./AlbumCard.module.css";

interface AlbumCardProps {
  albumId: number;
  artistId: number;
  title: string;
  coverUrl?: string | null;
  artistName?: string;
  releaseDate?: string | null;
  /** Small overlay in the top-left corner, e.g. chart rank or score. */
  badge?: React.ReactNode;
  /** Buttons rendered under the title (edit, delete, remove…). */
  actions?: React.ReactNode;
  footer?: React.ReactNode;
}

export default function AlbumCard({
  albumId,
  artistId,
  title,
  coverUrl,
  artistName,
  releaseDate,
  badge,
  actions,
  footer,
}: AlbumCardProps) {
  return (
    <article className={`${styles.card} fade-up`}>
      <Link href={albumHref(artistId, albumId)} className={`plain ${styles.link}`}>
        <div className={styles.media}>
          <Cover src={coverUrl} alt={`Cover of ${title}`} />
          {badge && <span className={styles.badge}>{badge}</span>}
        </div>
        <div className={styles.text}>
          <h3 className={styles.title}>{title}</h3>
          <p className={styles.meta}>
            {artistName}
            {artistName && releaseDate ? " · " : ""}
            {formatYear(releaseDate)}
          </p>
        </div>
      </Link>
      {footer && <div className={styles.footer}>{footer}</div>}
      {actions && <div className={styles.actions}>{actions}</div>}
    </article>
  );
}
