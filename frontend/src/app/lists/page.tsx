"use client";

import { useState } from "react";
import { ListMusic, Plus, Search } from "lucide-react";
import ListCard from "@/components/ListCard";
import Modal from "@/components/Modal";
import Pagination from "@/components/Pagination";
import RequireAuth from "@/components/RequireAuth";
import { SkeletonGrid } from "@/components/Skeletons";
import { EmptyState, ErrorState } from "@/components/States";
import ListForm from "@/components/forms/ListForm";
import { useAuth } from "@/context/AuthContext";
import { useDebounced } from "@/hooks/useDebounced";
import { useLoad } from "@/hooks/useLoad";
import { listApi } from "@/lib/endpoints";
import { useRouter } from "next/navigation";

export default function ListsPage() {
  return (
    <RequireAuth>
      <Lists />
    </RequireAuth>
  );
}

function Lists() {
  const router = useRouter();
  const { user } = useAuth();
  const [scope, setScope] = useState<"all" | "mine">("all");
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const [creating, setCreating] = useState(false);
  const name = useDebounced(search);

  const lists = useLoad(
    () =>
      scope === "mine"
        ? listApi.byUser(user!.id, { name, page, size: 12 })
        : listApi.list({ name, page, size: 12, sort: "updatedAt,desc" }),
    [scope, name, page, user?.id],
  );

  return (
    <>
      <header className="page-head">
        <div>
          <p className="eyebrow">Curated</p>
          <h1>Album lists</h1>
          <p>Rankings, themes and personal collections from the community.</p>
        </div>
        <button type="button" className="btn btn-primary" onClick={() => setCreating(true)}>
          <Plus size={18} aria-hidden /> New list
        </button>
      </header>

      <div className="toolbar">
        <fieldset className="choice-group" aria-label="Show">
          <label className="choice">
            <input
              type="radio"
              name="scope"
              checked={scope === "all"}
              onChange={() => {
                setScope("all");
                setPage(0);
              }}
            />
            All lists
          </label>
          <label className="choice">
            <input
              type="radio"
              name="scope"
              checked={scope === "mine"}
              onChange={() => {
                setScope("mine");
                setPage(0);
              }}
            />
            My lists
          </label>
        </fieldset>
        <div className="search">
          <Search size={18} aria-hidden />
          <label htmlFor="list-search" className="sr-only">
            Search lists
          </label>
          <input
            id="list-search"
            type="search"
            className="input"
            placeholder="Search by list name…"
            value={search}
            onChange={(event) => {
              setSearch(event.target.value);
              setPage(0);
            }}
          />
        </div>
      </div>

      {lists.error ? (
        <ErrorState message={lists.error} onRetry={lists.reload} />
      ) : lists.loading && !lists.data ? (
        <SkeletonGrid count={4} />
      ) : !lists.data?.content.length ? (
        <EmptyState title="No lists yet" icon={<ListMusic size={32} aria-hidden />}>
          <button type="button" className="btn btn-primary" onClick={() => setCreating(true)}>
            <Plus size={16} aria-hidden /> Create your first list
          </button>
        </EmptyState>
      ) : (
        <div className="grid">
          {lists.data.content.map((list) => (
            <ListCard key={list.id} list={list} />
          ))}
        </div>
      )}
      <Pagination data={lists.data} onPage={setPage} />

      <Modal open={creating} onClose={() => setCreating(false)} title="New list" icon={<ListMusic size={20} />}>
        {creating && (
          <ListForm
            onCancel={() => setCreating(false)}
            onDone={(list) => {
              setCreating(false);
              router.push(`/lists/${list.id}`);
            }}
          />
        )}
      </Modal>
    </>
  );
}
