import Link from "next/link";
import { ListMusic } from "lucide-react";
import Cover from "./Cover";
import { timeAgo } from "@/lib/format";
import type { AlbumListResponse } from "@/types/api";
import styles from "./ListCard.module.css";

/** Album list preview with a 2×2 mosaic of the first covers. */
export default function ListCard({ list, actions }: { list: AlbumListResponse; actions?: React.ReactNode }) {
  const covers = [...list.albums].sort((a, b) => a.position - b.position).slice(0, 4);

  return (
    <article className={`card card-hover fade-up ${styles.card}`}>
      <Link href={`/lists/${list.id}`} className={`plain ${styles.link}`}>
        <div className={styles.mosaic}>
          {covers.length ? (
            covers.map((item) => <Cover key={item.albumId} src={item.coverUrl} alt="" />)
          ) : (
            <div className={styles.empty}>
              <ListMusic size={32} aria-hidden />
            </div>
          )}
        </div>
        <div>
          <h3>{list.name}</h3>
          <p className="muted small">
            {list.albums.length} albums · by {list.username} · updated {timeAgo(list.updatedAt)}
          </p>
          {list.description && <p className={styles.desc}>{list.description}</p>}
        </div>
      </Link>
      {actions && <div className="row">{actions}</div>}
    </article>
  );
}
