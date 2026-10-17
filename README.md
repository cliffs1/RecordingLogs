# RecordingLogs

RecordingLogs is a music rating platform. Users can browse genres, artists, albums
and songs, rate and review albums, rate songs, create album lists, mark favorite
albums and follow other users to see their activity.

## Main database tables

| Table | Description |
|---|---|
| `genres` | Music genres |
| `artists` | Artists, linked to genres |
| `albums` | Albums, linked to artists, genres and songs |
| `songs` | Songs, linked to artists and genres |
| `users` | Platform users |
| `album_rating`, `song_rating` | User ratings (1–10) |
| `album_review` | Written album reviews |
| `album_lists` | User-created album lists |
| `refresh_tokens` | Hashed refresh tokens (rotation and logout) |
| `revoked_access_tokens` | Access tokens invalidated by logout |

## How to run

1. Install **JDK 21+** and **PostgreSQL**.
2. Create the database:
   ```sql
   CREATE DATABASE music_platform;
   ```
3. Set your PostgreSQL username and password in
   `backendRL/src/main/resources/application.properties`.
4. Start the application, either by running `BackendRlApplication` in IntelliJ or with:
   ```bash
   cd backendRL
   ./mvnw spring-boot:run
   ```

The API runs on **http://localhost:8080**. Tables are created automatically, and the
database is filled with demo data on first start.

For anything beyond local development, set the `JWT_SECRET` environment variable to a
random string of at least 32 characters. It replaces the development default in
`application.properties`.

> **Upgrading from a version without authentication:** drop and recreate the database
> (`DROP DATABASE music_platform; CREATE DATABASE music_platform;`). Old rows have
> plain-text passwords and no role, so the seeder must recreate the demo data.

### Demo accounts

All seeded accounts use the password `password123`.

| Username | Role |
|---|---|
| `admin` | ADMIN |
| `moderator` | MODERATOR |
| `rokas`, `egle`, `jonas`, `ieva`, `lukas` | USER |

## Authentication and authorization

Authentication uses **JWT** (HS256, signed and verified by the backend).

| Endpoint | Description |
|---|---|
| `POST /api/auth/register` | Create an account (role `USER`) |
| `POST /api/auth/login` | Log in with username or email. Returns an access token and sets the refresh token cookie |
| `POST /api/auth/refresh` | Exchange the refresh token cookie for a new access token |
| `POST /api/auth/logout` | Revoke the refresh token and the current access token |
| `GET /api/auth/me` | Profile of the logged-in user |

Send the access token with every protected request:
`Authorization: Bearer <accessToken>`.

### Tokens

- **Access token** lives for **15 minutes**. Its claims are `sub` (user ID), `role`,
  `username`, `jti` (unique token ID), `iss`, `iat` and `exp`.
- **Refresh token** lives for **7 days**. It is a random 256-bit value sent as an
  `HttpOnly`, `SameSite=Strict` cookie limited to `/api/auth`. Only its SHA-256 hash is
  stored in the database.
- **Rotation:** every `/refresh` call invalidates the used refresh token and issues a new
  one, so users stay logged in without logging in again. If an already-used refresh token
  is presented again (a sign it was stolen), all refresh tokens of that user are revoked.
- **Logout** revokes the refresh token and puts the access token's `jti` on a blacklist
  until it expires, so both stop working immediately.
- Changing a user's role or password revokes their refresh tokens.

### Roles and permissions

| Action | Guest | USER | MODERATOR | ADMIN |
|---|:-:|:-:|:-:|:-:|
| Browse genres, artists, albums, songs, charts, reviews, average ratings | ✔ | ✔ | ✔ | ✔ |
| Register / log in | ✔ | | | |
| View user profiles, lists, rating history, favorites, follows | | ✔ | ✔ | ✔ |
| Rate albums and songs, write reviews, create lists, favorite, follow | | ✔ | ✔ | ✔ |
| Edit or delete **own** ratings, reviews, lists, profile | | ✔ | ✔ | ✔ |
| View **own** activity feed | | ✔ | ✔ | ✔ |
| Create, edit and delete genres, artists, albums, songs | | | ✔ | ✔ |
| Delete **any** review or rating (moderation) | | | ✔ | ✔ |
| List all users, delete any account or list, view any feed | | | | ✔ |
| Change user roles (`PATCH /api/users/{id}/role`) | | | | ✔ |

Unauthenticated requests to protected endpoints get **401**. Requests that are not
allowed for the user's role get **403**. Ownership is enforced as well: the acting user
always comes from the token (request bodies no longer contain `userId`), and changing
someone else's content returns **403**, even for a regular user who has the right role.

## API documentation and tests

- Swagger UI: http://localhost:8080/swagger-ui/index.html. Log in with
  `POST /api/auth/login`, then paste the `accessToken` into **Authorize**.
- OpenAPI spec: http://localhost:8080/v3/api-docs
- Postman tests: import `postman/RecordingLogs.postman_collection.json` into Postman,
  then right-click the collection → **Run collection**. The collection registers its
  own users and logs in as them, the seeded `moderator` and the seeded `admin`. It
  covers the 401/403 cases, token refresh and logout.
