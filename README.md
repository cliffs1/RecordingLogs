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
3. Set the PostgreSQL password in the `DB_PASSWORD` environment variable (required).
   `DB_USERNAME` (default `postgres`) and `DB_URL`
   (default `jdbc:postgresql://localhost:5432/music_platform`) are optional.
   In IntelliJ, add them under *Run/Debug Configurations → Environment variables*.
   From PowerShell:
   ```powershell
   $env:DB_PASSWORD = "your-password"
   ```
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

## Frontend

The user interface is in `frontend/`. It is built with **Next.js 16 (App Router), React 19 and
TypeScript**, styled with plain CSS (global design tokens plus CSS Modules), uses
**lucide-react** SVG icons and the Google fonts **Space Grotesk** (headings) and **Inter** (body).

### How to run

1. Start the backend (see above) on http://localhost:8080.
2. Install **Node.js 20+**, then:
   ```bash
   cd frontend
   npm install
   cp .env.example .env.local   # BACKEND_URL=http://localhost:8080
   npm run dev
   ```
3. Open http://localhost:3000 and log in with a demo account (e.g. `admin` / `password123`).

`npm run build` and `npm run lint` must both pass before committing.

### How it talks to the API

- The browser only calls `/api/*` on the frontend's own origin. `next.config.ts` rewrites
  these requests to `BACKEND_URL`, so the `SameSite=Strict` refresh token cookie stays
  first-party and no CORS setup is needed. Swagger UI is proxied too (footer → "API docs").
- The access token is kept **in memory only** (`src/lib/api.ts`). On page load the app calls
  `POST /api/auth/refresh` to restore the session. When any request returns 401 it refreshes
  once and retries; concurrent refreshes share one request.
- All endpoint functions live in `src/lib/endpoints.ts`, one per backend method. DTO types are in
  `src/types/api.ts`.

### Pages and API coverage

| Page | API methods used |
|---|---|
| `/` Home | top albums / top songs charts (genre, year and min. ratings filters), activity feed |
| `/login`, `/register` | register, login, refresh, logout, me |
| `/genres`, `/genres/[id]` | genre CRUD; artists / albums / songs of a genre |
| `/artists`, `/artists/[id]` | artist CRUD, artist genres (attach/detach), album CRUD |
| `/artists/[a]/albums/[b]` | album get/edit/delete, album genres, song CRUD, song genres, album and song ratings (list, get own, create, update, delete), album reviews CRUD, favorite add/remove, add to list |
| `/reviews` | all reviews (search, date range, sort), review get/edit/delete |
| `/lists`, `/lists/[id]` | all lists, user lists, list CRUD, add/remove album |
| `/users/[id]` | user get/update/delete, follow/unfollow, followers, following, feed, reviews, album ratings, song ratings, lists, favorites |
| `/admin/users` | user list (admin), role change, delete |

Full CRUD is available for **genres, artists, albums, songs, reviews and lists**. Every
create/edit/delete form opens in a modal window (native `<dialog>`). Feedback is shown with
toast notifications and inline field errors (no `alert()`).

### UI requirements checklist

- **Responsive layout**: breakpoints at 768px (main) plus 1100px and 480px for grids. The menu
  turns into a hamburger slide-in panel below 768px.
- **Images**: album covers are fluid (`max-width: 100%`, square aspect ratio) and shrink with
  their grid cell. There is an SVG placeholder when a cover is missing.
- **Header / content / footer** each have their own style: a dark gradient header with pill
  links, a light content area with violet links and cards, and a cream footer with
  uppercase headings and underlined links.
- **Inputs**: text, email, password, url, search, number, date, range (1–10 rating),
  textarea, select, radio and checkbox.
- **Animations**: skeleton shimmer and spinners while loading, card hover lift, cover zoom,
  modal pop-in, menu slide-in, toast slide-in and a spinning vinyl. All respect
  `prefers-reduced-motion`.
- **Icons**: SVG icons (lucide-react), including in every menu item.

### Wireframes

Wireframes of the main screens go in `docs/wireframes/` (home, album page, create/edit
modal, login, mobile menu).

### Deployment

The project is deployed to **Microsoft Azure**:

- Website: https://recordinglogs-web-rokas.azurewebsites.net
- API (Swagger UI): https://recordinglogs-api-rokas.azurewebsites.net/swagger-ui/index.html

It was deployed with `deploy-azure.ps1` (Azure CLI):

| Resource | Azure service | Tier |
|---|---|---|
| Database | Azure Database for PostgreSQL flexible server | Burstable B1ms, 32 GB |
| Backend | App Service web app, Java 21 (Spring Boot jar) | Linux, Basic B1 plan |
| Frontend | App Service web app, Node 22 (Next.js standalone) | same B1 plan |

```powershell
az login
powershell -ExecutionPolicy Bypass -File .\deploy-azure.ps1 -Suffix <yourname>
# later, after code changes:
.\deploy-azure.ps1 -Suffix <yourname> -Only backend    # or -Only frontend
```

The script generates the DB password and JWT secret and stores them in the git-ignored file
`.azure-deploy.local.json`. The frontend is built with `BACKEND_URL` pointing to the deployed
API, because the `/api` rewrites are baked into the Next.js build.
