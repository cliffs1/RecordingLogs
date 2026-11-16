"use client";

import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { useState } from "react";
import { ListMusic, Pencil, Trash2, X } from "lucide-react";
import AlbumCard from "@/components/AlbumCard";
import ConfirmDialog from "@/components/ConfirmDialog";
import Modal from "@/components/Modal";
import RequireAuth from "@/components/RequireAuth";
import { PageLoader } from "@/components/Spinner";
import { EmptyState, ErrorState } from "@/components/States";
import ListForm from "@/components/forms/ListForm";
import { useAuth } from "@/context/AuthContext";
import { useLoad } from "@/hooks/useLoad";
import { listApi } from "@/lib/endpoints";
import { formatDate } from "@/lib/format";
import type { AlbumListItemResponse } from "@/types/api";

type Dialog = { kind: "edit" | "delete" } | { kind: "remove"; item: AlbumListItemResponse } | null;

export default function ListPage() {
  return (
    <RequireAuth>
      <ListDetail />
    </RequireAuth>
  );
}

function ListDetail() {
  const listId = Number(useParams<{ listId: string }>().listId);
  const router = useRouter();
  const { user, isAdmin } = useAuth();
  const [dialog, setDialog] = useState<Dialog>(null);
  const list = useLoad(() => listApi.get(listId), [listId]);
  const close = () => setDialog(null);

  if (list.error) return <ErrorState message={list.error} onRetry={list.reload} />;
  if (!list.data) return <PageLoader />;
  const l = list.data;
  const canModify = !!user && (user.id === l.userId || isAdmin);
  const items = [...l.albums].sort((a, b) => a.position - b.position);

  return (
    <>
      <header className="page-head">
        <div>
          <p className="eyebrow">
            <Link href="/lists">Lists</Link> / by <Link href={`/users/${l.userId}`}>{l.username}</Link>
          </p>
          <h1>{l.name}</h1>
          {l.description && <p style={{ maxWidth: "70ch" }}>{l.description}</p>}
          <p className="small">
            {items.length} albums · created {formatDate(l.createdAt)} · updated {formatDate(l.updatedAt)}
          </p>
        </div>
        {canModify && (
          <div className="row">
            <button type="button" className="btn btn-secondary" onClick={() => setDialog({ kind: "edit" })}>
              <Pencil size={16} aria-hidden /> Edit
            </button>
            <button type="button" className="btn btn-ghost" onClick={() => setDialog({ kind: "delete" })}>
              <Trash2 size={16} aria-hidden /> Delete
            </button>
          </div>
        )}
      </header>

      {!items.length ? (
        <EmptyState title="This list is empty" icon={<ListMusic size={32} aria-hidden />}>
          {canModify && <p>Open any album and choose “Add to list”.</p>}
        </EmptyState>
      ) : (
        <div className="grid">
          {items.map((item) => (
            <AlbumCard
              key={item.albumId}
              albumId={item.albumId}
              artistId={item.artistId}
              title={item.title}
              coverUrl={item.coverUrl}
              artistName={item.artistName}
              badge={<>#{item.position}</>}
              actions={
                canModify && (
                  <button
                    type="button"
                    className="btn btn-ghost btn-sm"
                    onClick={() => setDialog({ kind: "remove", item })}
                  >
                    <X size={14} aria-hidden /> Remove
                  </button>
                )
              }
            />
          ))}
        </div>
      )}

      <Modal open={dialog?.kind === "edit"} onClose={close} title="Edit list" icon={<ListMusic size={20} />}>
        {dialog?.kind === "edit" && (
          <ListForm
            list={l}
            onCancel={close}
            onDone={(updated) => {
              close();
              list.setData(updated);
            }}
          />
        )}
      </Modal>

      <ConfirmDialog
        open={dialog?.kind === "delete"}
        onClose={close}
        title="Delete list?"
        message={
          <>
            The list <strong>{l.name}</strong> will be deleted. The albums themselves stay in the catalog.
          </>
        }
        successMessage="List deleted"
        onConfirm={() => listApi.remove(listId)}
        onDone={() => router.push("/lists")}
      />

      <ConfirmDialog
        open={dialog?.kind === "remove"}
        onClose={close}
        title="Remove album from list?"
        confirmLabel="Remove"
        message={dialog?.kind === "remove" && <>“{dialog.item.title}” will be removed from this list.</>}
        successMessage="Album removed from list"
        onConfirm={() =>
          dialog?.kind === "remove" ? listApi.removeAlbum(listId, dialog.item.albumId) : Promise.resolve()
        }
        onDone={list.reload}
      />
    </>
  );
}
