// TypeScript mirrors of the backend DTOs (backendRL/src/main/java/org/example/backendrl/dto).
// Dates arrive as ISO strings: LocalDate -> "2024-05-01", LocalDateTime -> "2024-05-01T12:00:00".

export type Role = "USER" | "MODERATOR" | "ADMIN";

export interface Link {
  href: string;
}

export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  _links?: Record<string, Link>;
}

export interface PageQuery {
  page?: number;
  size?: number;
  sort?: string;
}

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  displayName: string | null;
  role: Role;
}

export interface TokenResponse {
  accessToken: string;
  expiresIn: number;
  user: UserResponse;
}

export interface GenreResponse {
  id: number;
  name: string;
}

export interface ArtistResponse {
  id: number;
  name: string;
  biography: string | null;
}

export interface ArtistSummaryResponse {
  id: number;
  name: string;
}

export interface AlbumResponse {
  id: number;
  artistId: number;
  title: string;
  releaseDate: string | null;
  coverUrl: string | null;
}

export interface AlbumSummaryResponse {
  id: number;
  title: string;
  releaseDate: string | null;
  coverUrl: string | null;
  artistId: number;
  artistName: string;
}

export interface SongResponse {
  id: number;
  albumId: number;
  title: string;
  durationSeconds: number | null;
  originalReleaseDate: string | null;
  trackNumber: number;
  discNumber: number | null;
}

export interface SongSummaryResponse {
  id: number;
  title: string;
  durationSeconds: number | null;
  originalReleaseDate: string | null;
  albumId: number;
  albumTitle: string;
  artistId: number;
  artistName: string;
}

export interface AlbumRatingResponse {
  id: number;
  userId: number;
  username: string;
  albumId: number;
  albumTitle: string;
  coverUrl: string | null;
  artistId: number;
  artistName: string;
  rating: number;
  createdAt: string;
  updatedAt: string;
}

export interface SongRatingResponse {
  id: number;
  userId: number;
  username: string;
  songId: number;
  songTitle: string;
  albumId: number;
  albumTitle: string;
  artistId: number;
  artistName: string;
  rating: number;
  createdAt: string;
  updatedAt: string;
}

export interface AlbumReviewResponse {
  id: number;
  userId: number;
  username: string;
  albumId: number;
  albumTitle: string;
  artistId: number;
  artistName: string;
  coverUrl: string | null;
  title: string;
  content: string;
  createdAt: string;
  updatedAt: string;
}

export interface AlbumListItemResponse {
  albumId: number;
  title: string;
  coverUrl: string | null;
  position: number;
  artistId: number;
  artistName: string;
}

export interface AlbumListResponse {
  id: number;
  userId: number;
  username: string;
  name: string;
  description: string | null;
  createdAt: string;
  updatedAt: string;
  albums: AlbumListItemResponse[];
}

export interface AlbumFavoriteResponse {
  id: number;
  userId: number;
  albumId: number;
  albumTitle: string;
  coverUrl: string | null;
  createdAt: string;
  artistId: number;
  artistName: string;
}

export interface UserFollowResponse {
  userId: number;
  username: string;
  displayName: string | null;
  followedAt: string;
}

export interface ActivityResponse {
  type: "ALBUM_RATING" | "SONG_RATING" | "ALBUM_REVIEW";
  albumId: number;
  artistId: number;
  userId: number;
  username: string;
  targetId: number;
  targetTitle: string;
  value: string | null;
  timestamp: string;
}

export interface TopAlbumResponse {
  rank: number;
  albumId: number;
  title: string;
  releaseDate: string | null;
  coverUrl: string | null;
  artistId: number;
  artistName: string;
  averageRating: number;
  ratingCount: number;
}

export interface TopSongResponse {
  rank: number;
  songId: number;
  title: string;
  durationSeconds: number | null;
  albumId: number;
  albumTitle: string;
  coverUrl: string | null;
  artistId: number;
  artistName: string;
  averageRating: number;
  ratingCount: number;
}

// ---- Request bodies ----

export interface LoginRequest {
  login: string;
  password: string;
}

export interface UserRequest {
  username: string;
  email: string;
  password: string;
  displayName?: string;
}

export interface UserUpdateRequest {
  username: string;
  email: string;
  password?: string;
  displayName?: string;
}

export interface GenreRequest {
  name: string;
}

export interface ArtistRequest {
  name: string;
  biography?: string;
}

export interface AlbumRequest {
  title: string;
  releaseDate?: string | null;
  coverUrl?: string | null;
}

export interface SongRequest {
  title: string;
  durationSeconds?: number | null;
  originalReleaseDate?: string | null;
  trackNumber: number;
  discNumber?: number | null;
}

export interface AlbumReviewRequest {
  title: string;
  content: string;
}

export interface AlbumListRequest {
  name: string;
  description?: string;
}
