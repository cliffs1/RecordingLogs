"use client";

import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { useState } from "react";
import {
  Activity,
  Disc3,
  Heart,
  ListMusic,
  Mail,
  MessageSquareText,
  Music2,
  Pencil,
  Star,
  Trash2,
  UserCheck,
  UserMinus,
  UserPlus,
  Users,
} from "lucide-react";
import ActivityFeed from "@/components/ActivityFeed";
import AlbumCard from "@/components/AlbumCard";
import ConfirmDialog from "@/components/ConfirmDialog";
import ListCard from "@/components/ListCard";
import Modal from "@/components/Modal";
import Pagination from "@/components/Pagination";
import RequireAuth from "@/components/RequireAuth";
import ReviewCard from "@/components/ReviewCard";
import ReviewModal from "@/components/ReviewModal";
import { PageLoader } from "@/components/Spinner";
import Spinner from "@/components/Spinner";
import { SkeletonList } from "@/components/Skeletons";
import { EmptyState, ErrorState } from "@/components/States";
import { useToast } from "@/components/Toast";
import UserForm from "@/components/forms/UserForm";
import { useAuth } from "@/context/AuthContext";
import { useLoad } from "@/hooks/useLoad";
import { listApi, reviewApi, userApi } from "@/lib/endpoints";
import { albumHref, errorMessage, initials, timeAgo } from "@/lib/format";
import type { Page, UserFollowResponse } from "@/types/api";
import styles from "./user.module.css";

type Tab = "reviews" | "ratings" | "songs" | "lists" | "favorites" | "followers" | "following" | "feed";

export default function UserPage() {
  return (
    <RequireAuth>
      <Profile />
    </RequireAuth>
  );
}

function Profile() {
  const userId = Number(useParams<{ userId: string }>().userId);
  const router = useRouter();
  const toast = useToast();
  const { user: me, isAdmin, logout, reloadMe } = useAuth();
  const [tab, setTab] = useState<Tab>("reviews");
  const [page, setPage] = useState(0);
  const [dialog, setDialog] = useState<"edit" | "delete" | null>(null);
  const [reviewId, setReviewId] = useState<number | null>(null);
  const [followBusy, setFollowBusy] = useState(false);

  const isSelf = me?.id === userId;
  const canManage = isSelf || isAdmin;

  const profile = useLoad(() => userApi.get(userId), [userId]);
  const followers = useLoad(() => userApi.followers(userId, { size: 1 }), [userId]);
  const following = useLoad(() => userApi.following(userId, { size: 1 }), [userId]);
  // Does the logged-in user follow this profile? (search my "following" by username)
  const myFollow = useLoad(
    () => userApi.following(me!.id, { username: profile.data?.username, size: 20 }),
    [me?.id, profile.data?.username],
    !!me && !isSelf && !!profile.data,
  );
  const isFollowing = !!myFollow.data?.content.some((f) => f.userId === userId);

  const query = { page, size: 12 };
  const reviews = useLoad(() => reviewApi.byUser(userId, query), [userId, page], tab === "reviews");
  const ratings = useLoad(() => userApi.albumRatings(userId, query), [userId, page], tab === "ratings");
  const songRatings = useLoad(() => userApi.songRatings(userId, query), [userId, page], tab === "songs");
  const lists = useLoad(() => listApi.byUser(userId, query), [userId, page], tab === "lists");
  const favorites = useLoad(() => userApi.favorites(userId, query), [userId, page], tab === "favorites");
  const followerList = useLoad(() => userApi.followers(userId, query), [userId, page], tab === "followers");
  const followingList = useLoad(() => userApi.following(userId, query), [userId, page], tab === "following");

  if (profile.error) return <ErrorState message={profile.error} onRetry={profile.reload} />;
  if (!profile.data) return <PageLoader />;
  const u = profile.data;

  async function toggleFollow() {
    if (!me) return;
    setFollowBusy(true);
    try {
      if (isFollowing) {
        await userApi.unfollow(me.id, userId);
        toast.success(`You unfollowed ${u.username}`);
      } else {
        await userApi.follow(me.id, userId);
        toast.success(`You now follow ${u.username}`);
      }
      myFollow.reload();
      followers.reload();
      if (tab === "followers") followerList.reload();
    } catch (error) {
      toast.error(errorMessage(error));
    } finally {
      setFollowBusy(false);
    }
  }

  const tabs: { id: Tab; label: string; icon: typeof Star; hidden?: boolean }[] = [
    { id: "reviews", label: "Reviews", icon: MessageSquareText },
    { id: "ratings", label: "Album ratings", icon: Star },
    { id: "songs", label: "Song ratings", icon: Music2 },
    { id: "lists", label: "Lists", icon: ListMusic },
    { id: "favorites", label: "Favorites", icon: Heart },
    { id: "followers", label: "Followers", icon: Users },
    { id: "following", label: "Following", icon: UserCheck },
    { id: "feed", label: "Feed", icon: Activity, hidden: !canManage },
  ];

  const openTab = (id: Tab) => {
    setTab(id);
    setPage(0);
  };

  return (
    <>
      <section className={styles.hero}>
        <span className={styles.avatar} aria-hidden>
          {initials(u.displayName || u.username)}
        </span>
        <div className={styles.info}>
          <div className="row">
            <h1 style={{ fontSize: "1.875rem" }}>{u.displayName || u.username}</h1>
            {u.role !== "USER" && (
              <span className={`badge badge-${u.role.toLowerCase()}`}>{u.role.toLowerCase()}</span>
            )}
          </div>
          <p className="muted">
            @{u.username}
            {canManage && (
              <>
                {" · "}
                <Mail size={14} aria-hidden style={{ verticalAlign: -2 }} /> {u.email}
              </>
            )}
          </p>
          <div className={styles.counts}>
            <button type="button" className={styles.count} onClick={() => openTab("followers")}>
              <strong>{followers.data?.totalElements ?? "–"}</strong> followers
            </button>
            <button type="button" className={styles.count} onClick={() => openTab("following")}>
              <strong>{following.data?.totalElements ?? "–"}</strong> following
            </button>
          </div>
        </div>
        <div className={styles.actions}>
          {!isSelf && me && (
            <button
              type="button"
              className={`btn ${isFollowing ? `btn-secondary ${styles.following}` : "btn-primary"}`}
              onClick={toggleFollow}
              disabled={followBusy || myFollow.loading}
            >
              {followBusy ? <Spinner size={16} /> : isFollowing ? <UserMinus size={16} aria-hidden /> : <UserPlus size={16} aria-hidden />}
              {isFollowing ? "Unfollow" : "Follow"}
            </button>
          )}
          {canManage && (
            <>
              <button type="button" className="btn btn-secondary" onClick={() => setDialog("edit")}>
                <Pencil size={16} aria-hidden /> Edit profile
              </button>
              <button type="button" className="btn btn-ghost" onClick={() => setDialog("delete")}>
                <Trash2 size={16} aria-hidden /> Delete account
              </button>
            </>
          )}
        </div>
      </section>

      <div className="tabs" role="tablist">
        {tabs
          .filter((t) => !t.hidden)
          .map(({ id, label, icon: Icon }) => (
            <button
              key={id}
              type="button"
              role="tab"
              className="tab"
              aria-selected={tab === id}
              onClick={() => openTab(id)}
            >
              <Icon size={16} aria-hidden /> {label}
            </button>
          ))}
      </div>

      {tab === "reviews" && (
        <TabBody state={reviews} empty="No reviews yet">
          {(data) => (
            <div className="grid grid-2">
              {data.content.map((review) => (
                <ReviewCard key={review.id} review={review} onOpen={() => setReviewId(review.id)} />
              ))}
            </div>
          )}
        </TabBody>
      )}

      {tab === "ratings" && (
        <TabBody state={ratings} empty="No album ratings yet">
          {(data) => (
            <div className="grid">
              {data.content.map((rating) => (
                <AlbumCard
                  key={rating.id}
                  albumId={rating.albumId}
                  artistId={rating.artistId}
                  title={rating.albumTitle}
                  coverUrl={rating.coverUrl}
                  artistName={rating.artistName}
                  badge={
                    <>
                      <Star size={12} fill="currentColor" aria-hidden /> {rating.rating}
                    </>
                  }
                  footer={<>rated {timeAgo(rating.updatedAt)}</>}
                />
              ))}
            </div>
          )}
        </TabBody>
      )}

      {tab === "songs" && (
        <TabBody state={songRatings} empty="No song ratings yet">
          {(data) => (
            <ul className="list card" style={{ padding: 0 }}>
              {data.content.map((rating) => (
                <li key={rating.id} className="list-item">
                  <span className="score" style={{ minWidth: 40, height: 40, fontSize: 15 }}>
                    {rating.rating}
                  </span>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <strong>{rating.songTitle}</strong>
                    <p className="muted small">
                      <Link href={`/artists/${rating.artistId}`}>{rating.artistName}</Link> ·{" "}
                      <Link href={albumHref(rating.artistId, rating.albumId)}>{rating.albumTitle}</Link>
                    </p>
                  </div>
                  <span className="muted small">{timeAgo(rating.updatedAt)}</span>
                </li>
              ))}
            </ul>
          )}
        </TabBody>
      )}

      {tab === "lists" && (
        <TabBody state={lists} empty="No lists yet">
          {(data) => (
            <div className="grid">
              {data.content.map((list) => (
                <ListCard key={list.id} list={list} />
              ))}
            </div>
          )}
        </TabBody>
      )}

      {tab === "favorites" && (
        <TabBody state={favorites} empty="No favorite albums yet">
          {(data) => (
            <div className="grid">
              {data.content.map((fav) => (
                <AlbumCard
                  key={fav.id}
                  albumId={fav.albumId}
                  artistId={fav.artistId}
                  title={fav.albumTitle}
                  coverUrl={fav.coverUrl}
                  artistName={fav.artistName}
                  badge={<Heart size={14} fill="currentColor" aria-label="Favorite" />}
                  footer={<>added {timeAgo(fav.createdAt)}</>}
                />
              ))}
            </div>
          )}
        </TabBody>
      )}

      {tab === "followers" && (
        <TabBody state={followerList} empty="No followers yet">
          {(data) => <PeopleList data={data} />}
        </TabBody>
      )}

      {tab === "following" && (
        <TabBody state={followingList} empty="Not following anyone yet">
          {(data) => <PeopleList data={data} />}
        </TabBody>
      )}

      {tab === "feed" && canManage && <ActivityFeed userId={userId} />}

      {tab !== "feed" && (
        <Pagination
          data={
            {
              reviews: reviews.data,
              ratings: ratings.data,
              songs: songRatings.data,
              lists: lists.data,
              favorites: favorites.data,
              followers: followerList.data,
              following: followingList.data,
            }[tab]
          }
          onPage={setPage}
        />
      )}

      <ReviewModal reviewId={reviewId} onClose={() => setReviewId(null)} />

      <Modal open={dialog === "edit"} onClose={() => setDialog(null)} title="Edit profile" icon={<Pencil size={20} />}>
        {dialog === "edit" && (
          <UserForm
            user={u}
            onCancel={() => setDialog(null)}
            onDone={(updated) => {
              setDialog(null);
              profile.setData(updated);
              if (isSelf) void reloadMe();
            }}
          />
        )}
      </Modal>

      <ConfirmDialog
        open={dialog === "delete"}
        onClose={() => setDialog(null)}
        title={isSelf ? "Delete your account?" : `Delete ${u.username}?`}
        message="All ratings, reviews, lists and follows of this account will be permanently deleted."
        confirmLabel="Delete account"
        successMessage="Account deleted"
        onConfirm={() => userApi.remove(userId)}
        onDone={async () => {
          if (isSelf) {
            await logout().catch(() => undefined);
            router.push("/");
          } else {
            router.push("/admin/users");
          }
        }}
      />
    </>
  );
}

function TabBody<T>({
  state,
  empty,
  children,
}: {
  state: { data: Page<T> | undefined; loading: boolean; error: string | null; reload: () => void };
  empty: string;
  children: (data: Page<T>) => React.ReactNode;
}) {
  if (state.error) return <ErrorState message={state.error} onRetry={state.reload} />;
  if (state.loading || !state.data) return <SkeletonList />;
  if (!state.data.content.length) return <EmptyState title={empty} icon={<Disc3 size={32} aria-hidden />} />;
  return <>{children(state.data)}</>;
}

function PeopleList({ data }: { data: Page<UserFollowResponse> }) {
  return (
    <ul className="list card" style={{ padding: 0 }}>
      {data.content.map((person) => (
        <li key={person.userId} className="list-item">
          <span className="score" style={{ minWidth: 40, height: 40, fontSize: 14, borderRadius: "50%" }}>
            {initials(person.displayName || person.username)}
          </span>
          <div style={{ flex: 1, minWidth: 0 }}>
            <Link href={`/users/${person.userId}`}>{person.displayName || person.username}</Link>
            <p className="muted small">@{person.username}</p>
          </div>
          <span className="muted small">since {timeAgo(person.followedAt)}</span>
        </li>
      ))}
    </ul>
  );
}
