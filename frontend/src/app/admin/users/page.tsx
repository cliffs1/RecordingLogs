"use client";

import Link from "next/link";
import { useState } from "react";
import { Search, ShieldCheck, Trash2, UserCog } from "lucide-react";
import ConfirmDialog from "@/components/ConfirmDialog";
import Modal from "@/components/Modal";
import Pagination from "@/components/Pagination";
import RequireAuth from "@/components/RequireAuth";
import { SkeletonList } from "@/components/Skeletons";
import { EmptyState, ErrorState } from "@/components/States";
import RoleForm from "@/components/forms/RoleForm";
import { useAuth } from "@/context/AuthContext";
import { useDebounced } from "@/hooks/useDebounced";
import { useLoad } from "@/hooks/useLoad";
import { userApi } from "@/lib/endpoints";
import type { UserResponse } from "@/types/api";

type Dialog = { kind: "role" | "delete"; user: UserResponse } | null;

export default function AdminUsersPage() {
  return (
    <RequireAuth roles={["ADMIN"]}>
      <AdminUsers />
    </RequireAuth>
  );
}

function AdminUsers() {
  const { user: me } = useAuth();
  const [search, setSearch] = useState("");
  const [field, setField] = useState<"username" | "displayName">("username");
  const [sort, setSort] = useState("username,asc");
  const [page, setPage] = useState(0);
  const [dialog, setDialog] = useState<Dialog>(null);
  const term = useDebounced(search);

  const users = useLoad(
    () => userApi.list({ [field]: term, sort, page, size: 15 }),
    [field, term, sort, page],
  );
  const close = () => setDialog(null);

  return (
    <>
      <header className="page-head">
        <div>
          <p className="eyebrow">
            <ShieldCheck size={12} aria-hidden /> Administration
          </p>
          <h1>Users</h1>
          <p>Manage accounts and roles.</p>
        </div>
      </header>

      <div className="toolbar">
        <div className="field">
          <label className="label" htmlFor="u-field">
            Search in
          </label>
          <select
            id="u-field"
            className="select"
            value={field}
            onChange={(event) => setField(event.target.value as typeof field)}
          >
            <option value="username">Username</option>
            <option value="displayName">Display name</option>
          </select>
        </div>
        <div className="search" style={{ alignSelf: "flex-end" }}>
          <Search size={18} aria-hidden />
          <label htmlFor="u-search" className="sr-only">
            Search users
          </label>
          <input
            id="u-search"
            type="search"
            className="input"
            placeholder="Search…"
            value={search}
            onChange={(event) => {
              setSearch(event.target.value);
              setPage(0);
            }}
          />
        </div>
        <div className="field">
          <label className="label" htmlFor="u-sort">
            Sort
          </label>
          <select id="u-sort" className="select" value={sort} onChange={(event) => setSort(event.target.value)}>
            <option value="username,asc">Username A–Z</option>
            <option value="username,desc">Username Z–A</option>
            <option value="id,desc">Newest first</option>
          </select>
        </div>
      </div>

      {users.error ? (
        <ErrorState message={users.error} onRetry={users.reload} />
      ) : users.loading && !users.data ? (
        <SkeletonList />
      ) : !users.data?.content.length ? (
        <EmptyState title="No users found" />
      ) : (
        <div className="card table-wrap" style={{ padding: 0 }}>
          <table className="table">
            <thead>
              <tr>
                <th scope="col">User</th>
                <th scope="col">Email</th>
                <th scope="col">Role</th>
                <th scope="col">
                  <span className="sr-only">Actions</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {users.data.content.map((user) => (
                <tr key={user.id}>
                  <td>
                    <Link href={`/users/${user.id}`}>{user.username}</Link>
                    {user.displayName && <div className="muted small">{user.displayName}</div>}
                  </td>
                  <td className="muted">{user.email}</td>
                  <td>
                    <span className={`badge badge-${user.role.toLowerCase()}`}>{user.role.toLowerCase()}</span>
                  </td>
                  <td>
                    <div className="row" style={{ justifyContent: "flex-end", gap: 4, flexWrap: "nowrap" }}>
                      <button
                        type="button"
                        className="btn btn-ghost btn-sm"
                        onClick={() => setDialog({ kind: "role", user })}
                        disabled={user.id === me?.id}
                        title={user.id === me?.id ? "You cannot change your own role" : undefined}
                      >
                        <UserCog size={14} aria-hidden /> Role
                      </button>
                      <button
                        type="button"
                        className="btn btn-ghost btn-sm btn-icon"
                        onClick={() => setDialog({ kind: "delete", user })}
                        disabled={user.id === me?.id}
                        aria-label={`Delete ${user.username}`}
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      <Pagination data={users.data} onPage={setPage} />

      <Modal open={dialog?.kind === "role"} onClose={close} title="Change role" icon={<UserCog size={20} />} size="sm">
        {dialog?.kind === "role" && (
          <RoleForm
            user={dialog.user}
            onCancel={close}
            onDone={() => {
              close();
              users.reload();
            }}
          />
        )}
      </Modal>

      <ConfirmDialog
        open={dialog?.kind === "delete"}
        onClose={close}
        title="Delete user?"
        message={
          dialog?.kind === "delete" && (
            <>
              <strong>{dialog.user.username}</strong> and all of their ratings, reviews and lists will be deleted.
            </>
          )
        }
        successMessage="User deleted"
        onConfirm={() => (dialog?.kind === "delete" ? userApi.remove(dialog.user.id) : Promise.resolve())}
        onDone={users.reload}
      />
    </>
  );
}
