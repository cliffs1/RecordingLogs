import { api, setAccessToken } from "./api";
import type {
  ActivityResponse,
  AlbumFavoriteResponse,
  AlbumListRequest,
  AlbumListResponse,
  AlbumRatingResponse,
  AlbumRequest,
  AlbumResponse,
  AlbumReviewRequest,
  AlbumReviewResponse,
  AlbumSummaryResponse,
  ArtistRequest,
  ArtistResponse,
  ArtistSummaryResponse,
  GenreRequest,
  GenreResponse,
  LoginRequest,
  Page,
  PageQuery,
  Role,
  SongRatingResponse,
  SongRequest,
  SongResponse,
  SongSummaryResponse,
  TokenResponse,
  TopAlbumResponse,
  TopSongResponse,
  UserFollowResponse,
  UserRequest,
  UserResponse,
  UserUpdateRequest,
} from "@/types/api";

type Q = PageQuery & Record<string, string | number | undefined>;

// ---------- Auth ----------
export const authApi = {
  register: (body: UserRequest) =>
    api<UserResponse>("/auth/register", { method: "POST", body, noRetry: true }),
  login: async (body: LoginRequest) => {
    const session = await api<TokenResponse>("/auth/login", { method: "POST", body, noRetry: true });
    setAccessToken(session.accessToken);
    return session;
  },
  logout: () => api<void>("/auth/logout", { method: "POST" }),
  me: () => api<UserResponse>("/auth/me"),
};

// ---------- Genres ----------
export const genreApi = {
  list: (query?: Q) => api<Page<GenreResponse>>("/genres", { query }),
  get: (id: number) => api<GenreResponse>(`/genres/${id}`),
  create: (body: GenreRequest) => api<GenreResponse>("/genres", { method: "POST", body }),
  update: (id: number, body: GenreRequest) =>
    api<GenreResponse>(`/genres/${id}`, { method: "PUT", body }),
  remove: (id: number) => api<void>(`/genres/${id}`, { method: "DELETE" }),
  artists: (id: number, query?: Q) =>
    api<Page<ArtistSummaryResponse>>(`/genres/${id}/artists`, { query }),
  albums: (id: number, query?: Q) =>
    api<Page<AlbumSummaryResponse>>(`/genres/${id}/albums`, { query }),
  songs: (id: number, query?: Q) =>
    api<Page<SongSummaryResponse>>(`/genres/${id}/songs`, { query }),
};

// ---------- Artists ----------
export const artistApi = {
  list: (query?: Q) => api<Page<ArtistResponse>>("/artists", { query }),
  get: (id: number) => api<ArtistResponse>(`/artists/${id}`),
  create: (body: ArtistRequest) => api<ArtistResponse>("/artists", { method: "POST", body }),
  update: (id: number, body: ArtistRequest) =>
    api<ArtistResponse>(`/artists/${id}`, { method: "PUT", body }),
  remove: (id: number) => api<void>(`/artists/${id}`, { method: "DELETE" }),
  genres: (id: number, query?: Q) => api<Page<GenreResponse>>(`/artists/${id}/genres`, { query }),
  addGenre: (id: number, genreId: number) =>
    api<void>(`/artists/${id}/genres/${genreId}`, { method: "POST" }),
  removeGenre: (id: number, genreId: number) =>
    api<void>(`/artists/${id}/genres/${genreId}`, { method: "DELETE" }),
};

// ---------- Albums (nested under artists) ----------
export const albumApi = {
  list: (artistId: number, query?: Q) =>
    api<Page<AlbumResponse>>(`/artists/${artistId}/albums`, { query }),
  get: (artistId: number, id: number) => api<AlbumResponse>(`/artists/${artistId}/albums/${id}`),
  create: (artistId: number, body: AlbumRequest) =>
    api<AlbumResponse>(`/artists/${artistId}/albums`, { method: "POST", body }),
  update: (artistId: number, id: number, body: AlbumRequest) =>
    api<AlbumResponse>(`/artists/${artistId}/albums/${id}`, { method: "PUT", body }),
  remove: (artistId: number, id: number) =>
    api<void>(`/artists/${artistId}/albums/${id}`, { method: "DELETE" }),
  genres: (albumId: number, query?: Q) =>
    api<Page<GenreResponse>>(`/albums/${albumId}/genres`, { query }),
  addGenre: (albumId: number, genreId: number) =>
    api<void>(`/albums/${albumId}/genres/${genreId}`, { method: "POST" }),
  removeGenre: (albumId: number, genreId: number) =>
    api<void>(`/albums/${albumId}/genres/${genreId}`, { method: "DELETE" }),
};

// ---------- Songs (nested under albums) ----------
export const songApi = {
  list: (artistId: number, albumId: number, query?: Q) =>
    api<Page<SongResponse>>(`/artists/${artistId}/albums/${albumId}/songs`, { query }),
  get: (artistId: number, albumId: number, id: number) =>
    api<SongResponse>(`/artists/${artistId}/albums/${albumId}/songs/${id}`),
  create: (artistId: number, albumId: number, body: SongRequest) =>
    api<SongResponse>(`/artists/${artistId}/albums/${albumId}/songs`, { method: "POST", body }),
  update: (artistId: number, albumId: number, id: number, body: SongRequest) =>
    api<SongResponse>(`/artists/${artistId}/albums/${albumId}/songs/${id}`, { method: "PUT", body }),
  remove: (artistId: number, albumId: number, id: number) =>
    api<void>(`/artists/${artistId}/albums/${albumId}/songs/${id}`, { method: "DELETE" }),
  genres: (songId: number, query?: Q) =>
    api<Page<GenreResponse>>(`/songs/${songId}/genres`, { query }),
  addGenre: (songId: number, genreId: number) =>
    api<void>(`/songs/${songId}/genres/${genreId}`, { method: "POST" }),
  removeGenre: (songId: number, genreId: number) =>
    api<void>(`/songs/${songId}/genres/${genreId}`, { method: "DELETE" }),
};

// ---------- Ratings ----------
export const albumRatingApi = {
  list: (albumId: number, query?: Q) =>
    api<Page<AlbumRatingResponse>>(`/albums/${albumId}/ratings`, { query }),
  get: (albumId: number, userId: number) =>
    api<AlbumRatingResponse>(`/albums/${albumId}/ratings/${userId}`),
  create: (albumId: number, rating: number) =>
    api<AlbumRatingResponse>(`/albums/${albumId}/ratings`, { method: "POST", body: { rating } }),
  update: (albumId: number, userId: number, rating: number) =>
    api<AlbumRatingResponse>(`/albums/${albumId}/ratings/${userId}`, {
      method: "PUT",
      body: { rating },
    }),
  remove: (albumId: number, userId: number) =>
    api<void>(`/albums/${albumId}/ratings/${userId}`, { method: "DELETE" }),
};

export const songRatingApi = {
  list: (songId: number, query?: Q) =>
    api<Page<SongRatingResponse>>(`/songs/${songId}/ratings`, { query }),
  get: (songId: number, userId: number) =>
    api<SongRatingResponse>(`/songs/${songId}/ratings/${userId}`),
  create: (songId: number, rating: number) =>
    api<SongRatingResponse>(`/songs/${songId}/ratings`, { method: "POST", body: { rating } }),
  update: (songId: number, userId: number, rating: number) =>
    api<SongRatingResponse>(`/songs/${songId}/ratings/${userId}`, {
      method: "PUT",
      body: { rating },
    }),
  remove: (songId: number, userId: number) =>
    api<void>(`/songs/${songId}/ratings/${userId}`, { method: "DELETE" }),
};

// ---------- Reviews ----------
export const reviewApi = {
  list: (query?: Q) => api<Page<AlbumReviewResponse>>("/reviews", { query }),
  get: (id: number) => api<AlbumReviewResponse>(`/reviews/${id}`),
  byAlbum: (albumId: number, query?: Q) =>
    api<Page<AlbumReviewResponse>>(`/albums/${albumId}/reviews`, { query }),
  byUser: (userId: number, query?: Q) =>
    api<Page<AlbumReviewResponse>>(`/users/${userId}/reviews`, { query }),
  create: (albumId: number, body: AlbumReviewRequest) =>
    api<AlbumReviewResponse>(`/albums/${albumId}/reviews`, { method: "POST", body }),
  update: (id: number, body: AlbumReviewRequest) =>
    api<AlbumReviewResponse>(`/reviews/${id}`, { method: "PUT", body }),
  remove: (id: number) => api<void>(`/reviews/${id}`, { method: "DELETE" }),
};

// ---------- Album lists ----------
export const listApi = {
  list: (query?: Q) => api<Page<AlbumListResponse>>("/lists", { query }),
  get: (id: number) => api<AlbumListResponse>(`/lists/${id}`),
  byUser: (userId: number, query?: Q) =>
    api<Page<AlbumListResponse>>(`/users/${userId}/lists`, { query }),
  create: (body: AlbumListRequest) => api<AlbumListResponse>("/lists", { method: "POST", body }),
  update: (id: number, body: AlbumListRequest) =>
    api<AlbumListResponse>(`/lists/${id}`, { method: "PUT", body }),
  remove: (id: number) => api<void>(`/lists/${id}`, { method: "DELETE" }),
  addAlbum: (id: number, albumId: number, position?: number) =>
    api<AlbumListResponse>(`/lists/${id}/albums/${albumId}`, {
      method: "POST",
      body: position ? { position } : undefined,
    }),
  removeAlbum: (id: number, albumId: number) =>
    api<void>(`/lists/${id}/albums/${albumId}`, { method: "DELETE" }),
};

// ---------- Users ----------
export const userApi = {
  list: (query?: Q) => api<Page<UserResponse>>("/users", { query }),
  get: (id: number) => api<UserResponse>(`/users/${id}`),
  update: (id: number, body: UserUpdateRequest) =>
    api<UserResponse>(`/users/${id}`, { method: "PUT", body }),
  updateRole: (id: number, role: Role) =>
    api<UserResponse>(`/users/${id}/role`, { method: "PATCH", body: { role } }),
  remove: (id: number) => api<void>(`/users/${id}`, { method: "DELETE" }),

  favorites: (userId: number, query?: Q) =>
    api<Page<AlbumFavoriteResponse>>(`/users/${userId}/favorites`, { query }),
  addFavorite: (userId: number, albumId: number) =>
    api<AlbumFavoriteResponse>(`/users/${userId}/favorites/${albumId}`, { method: "POST" }),
  removeFavorite: (userId: number, albumId: number) =>
    api<void>(`/users/${userId}/favorites/${albumId}`, { method: "DELETE" }),

  following: (userId: number, query?: Q) =>
    api<Page<UserFollowResponse>>(`/users/${userId}/following`, { query }),
  followers: (userId: number, query?: Q) =>
    api<Page<UserFollowResponse>>(`/users/${userId}/followers`, { query }),
  follow: (userId: number, targetId: number) =>
    api<UserFollowResponse>(`/users/${userId}/following/${targetId}`, { method: "POST" }),
  unfollow: (userId: number, targetId: number) =>
    api<void>(`/users/${userId}/following/${targetId}`, { method: "DELETE" }),
  feed: (userId: number, query?: Q) =>
    api<Page<ActivityResponse>>(`/users/${userId}/feed`, { query }),

  albumRatings: (userId: number, query?: Q) =>
    api<Page<AlbumRatingResponse>>(`/users/${userId}/ratings`, { query }),
  songRatings: (userId: number, query?: Q) =>
    api<Page<SongRatingResponse>>(`/users/${userId}/song-ratings`, { query }),
};

// ---------- Charts ----------
export const chartApi = {
  topAlbums: (query?: Q) => api<Page<TopAlbumResponse>>("/charts/top-albums", { query }),
  topSongs: (query?: Q) => api<Page<TopSongResponse>>("/charts/top-songs", { query }),
};
