"use client";

import Link from "next/link";
import { useState } from "react";
import { MessageSquareText, Music2, Star } from "lucide-react";
import { useLoad } from "@/hooks/useLoad";
import { userApi } from "@/lib/endpoints";
import { albumHref, timeAgo } from "@/lib/format";
import Pagination from "./Pagination";
import { SkeletonList } from "./Skeletons";
import { EmptyState, ErrorState } from "./States";

const TYPES = [
  { value: "", label: "All activity" },
  { value: "ALBUM_RATING", label: "Album ratings" },
  { value: "SONG_RATING", label: "Song ratings" },
  { value: "ALBUM_REVIEW", label: "Reviews" },
];

const ICON = { ALBUM_RATING: Star, SONG_RATING: Music2, ALBUM_REVIEW: MessageSquareText };

/** GET /api/users/{id}/feed — activity of the people the user follows. */
export default function ActivityFeed({ userId }: { userId: number }) {
  const [type, setType] = useState("");
  const [page, setPage] = useState(0);
  const feed = useLoad(() => userApi.feed(userId, { type, page, size: 10 }), [userId, type, page]);

  return (
    <div className="stack">
      <div className="row">
        <label htmlFor="feed-type" className="sr-only">
          Activity type
        </label>
        <select
          id="feed-type"
          className="select"
          style={{ width: "auto" }}
          value={type}
          onChange={(event) => {
            setType(event.target.value);
            setPage(0);
          }}
        >
          {TYPES.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
      </div>

      {feed.error ? (
        <ErrorState message={feed.error} onRetry={feed.reload} />
      ) : feed.loading && !feed.data ? (
        <SkeletonList />
      ) : !feed.data?.content.length ? (
        <EmptyState title="Nothing here yet">
          <p>Follow other listeners to see their ratings and reviews here.</p>
        </EmptyState>
      ) : (
        <ul className="list card" style={{ padding: 0 }}>
          {feed.data.content.map((item, index) => {
            const Icon = ICON[item.type] ?? Star;
            return (
              <li key={`${item.type}-${item.targetId}-${index}`} className="list-item fade-up">
                <span className="score" style={{ minWidth: 40, height: 40, fontSize: 14 }}>
                  {item.type === "ALBUM_REVIEW" ? <Icon size={18} /> : item.value}
                </span>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <Link href={`/users/${item.userId}`}>{item.username}</Link>{" "}
                  <span className="muted">
                    {item.type === "ALBUM_REVIEW"
                      ? "reviewed"
                      : item.type === "SONG_RATING"
                        ? "rated the song"
                        : "rated"}
                  </span>{" "}
                  <Link href={albumHref(item.artistId, item.albumId)}>{item.targetTitle}</Link>
                  {item.type === "ALBUM_REVIEW" && item.value && (
                    <p className="muted small">“{item.value}”</p>
                  )}
                </div>
                <span className="muted small" style={{ whiteSpace: "nowrap" }}>
                  <Icon size={14} aria-hidden style={{ verticalAlign: -2 }} /> {timeAgo(item.timestamp)}
                </span>
              </li>
            );
          })}
        </ul>
      )}
      <Pagination data={feed.data} onPage={setPage} />
    </div>
  );
}
