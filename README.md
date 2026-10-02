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

## API documentation and tests

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI spec: http://localhost:8080/v3/api-docs
- Postman tests: import `postman/RecordingLogs.postman_collection.json` into Postman,
  then right-click the collection → **Run collection**.
