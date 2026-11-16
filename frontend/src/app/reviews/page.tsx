"use client";

import { useState } from "react";
import { MessageSquarePlus, MessageSquareText, Search } from "lucide-react";
import ConfirmDialog from "@/components/ConfirmDialog";
import Modal from "@/components/Modal";
import Pagination from "@/components/Pagination";
import ReviewCard from "@/components/ReviewCard";
import ReviewModal from "@/components/ReviewModal";
import { SkeletonList } from "@/components/Skeletons";
import { EmptyState, ErrorState } from "@/components/States";
import ReviewForm from "@/components/forms/ReviewForm";
import { useAuth } from "@/context/AuthContext";
import { useDebounced } from "@/hooks/useDebounced";
import { useLoad } from "@/hooks/useLoad";
import { reviewApi } from "@/lib/endpoints";
import type { AlbumReviewResponse } from "@/types/api";

type Dialog = { kind: "edit" | "delete"; review: AlbumReviewResponse } | null;

export default function ReviewsPage() {
  const { user, isAdmin } = useAuth();
  const [search, setSearch] = useState("");
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
  const [sort, setSort] = useState("createdAt,desc");
  const [page, setPage] = useState(0);
  const [reviewId, setReviewId] = useState<number | null>(null);
  const [dialog, setDialog] = useState<Dialog>(null);
  const title = useDebounced(search);

  const reviews = useLoad(
    () => reviewApi.list({ title, from, to, sort, page, size: 10 }),
    [title, from, to, sort, page],
  );
  const close = () => setDialog(null);
  const resetPage = () => setPage(0);

  return (
    <>
      <header className="page-head">
        <div>
          <p className="eyebrow">Community</p>
          <h1>Latest reviews</h1>
          <p>What listeners are writing about. Open an album to write your own.</p>
        </div>
      </header>

      <div className="toolbar">
        <div className="search">
          <Search size={18} aria-hidden />
          <label htmlFor="review-search" className="sr-only">
            Search review titles
          </label>
          <input
            id="review-search"
            type="search"
            className="input"
            placeholder="Search review headlines…"
            value={search}
            onChange={(event) => {
              setSearch(event.target.value);
              resetPage();
            }}
          />
        </div>
        <div className="field">
          <label className="label" htmlFor="rev-from">
            From
          </label>
          <input
            id="rev-from"
            type="date"
            className="input"
            value={from}
            onChange={(event) => {
              setFrom(event.target.value);
              resetPage();
            }}
          />
        </div>
        <div className="field">
          <label className="label" htmlFor="rev-to">
            To
          </label>
          <input
            id="rev-to"
            type="date"
            className="input"
            value={to}
            onChange={(event) => {
              setTo(event.target.value);
              resetPage();
            }}
          />
        </div>
        <div className="field">
          <label className="label" htmlFor="rev-sort">
            Sort
          </label>
          <select id="rev-sort" className="select" value={sort} onChange={(event) => setSort(event.target.value)}>
            <option value="createdAt,desc">Newest first</option>
            <option value="createdAt,asc">Oldest first</option>
            <option value="title,asc">Headline A–Z</option>
          </select>
        </div>
      </div>

      {reviews.error ? (
        <ErrorState message={reviews.error} onRetry={reviews.reload} />
      ) : reviews.loading && !reviews.data ? (
        <SkeletonList />
      ) : !reviews.data?.content.length ? (
        <EmptyState title="No reviews match" icon={<MessageSquareText size={32} aria-hidden />} />
      ) : (
        <div className="grid grid-2">
          {reviews.data.content.map((review) => (
            <ReviewCard
              key={review.id}
              review={review}
              canModify={!!user && (user.id === review.userId || isAdmin)}
              onOpen={() => setReviewId(review.id)}
              onEdit={() => setDialog({ kind: "edit", review })}
              onDelete={() => setDialog({ kind: "delete", review })}
            />
          ))}
        </div>
      )}
      <Pagination data={reviews.data} onPage={setPage} />

      <ReviewModal reviewId={reviewId} onClose={() => setReviewId(null)} />

      <Modal
        open={dialog?.kind === "edit"}
        onClose={close}
        title="Edit review"
        description={dialog?.review.albumTitle}
        icon={<MessageSquarePlus size={20} />}
        size="lg"
      >
        {dialog?.kind === "edit" && (
          <ReviewForm
            albumId={dialog.review.albumId}
            review={dialog.review}
            onCancel={close}
            onDone={() => {
              close();
              reviews.reload();
            }}
          />
        )}
      </Modal>

      <ConfirmDialog
        open={dialog?.kind === "delete"}
        onClose={close}
        title="Delete review?"
        message={dialog?.kind === "delete" && <>“{dialog.review.title}” will be permanently deleted.</>}
        successMessage="Review deleted"
        onConfirm={() => (dialog?.kind === "delete" ? reviewApi.remove(dialog.review.id) : Promise.resolve())}
        onDone={reviews.reload}
      />
    </>
  );
}
