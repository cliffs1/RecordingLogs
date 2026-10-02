package org.example.backendrl.config;

import org.example.backendrl.entity.*;
import org.example.backendrl.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Fills an empty database with demo data so the API can be demonstrated.
 * Runs only when there are no users yet. Disable with app.seed.enabled=false.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final ArtistGenreRepository artistGenreRepository;
    private final ArtistSongRepository artistSongRepository;
    private final UserRepository userRepository;
    private final AlbumRatingRepository albumRatingRepository;
    private final SongRatingRepository songRatingRepository;
    private final AlbumReviewRepository albumReviewRepository;
    private final AlbumListRepository albumListRepository;
    private final AlbumFavoriteRepository albumFavoriteRepository;
    private final UserFollowRepository userFollowRepository;

    private final Map<String, Genre> genres = new HashMap<>();
    private final Map<String, Album> albums = new HashMap<>();
    private final Map<String, Song> songs = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();

    public DataSeeder(
            GenreRepository genreRepository,
            ArtistRepository artistRepository,
            AlbumRepository albumRepository,
            SongRepository songRepository,
            ArtistGenreRepository artistGenreRepository,
            ArtistSongRepository artistSongRepository,
            UserRepository userRepository,
            AlbumRatingRepository albumRatingRepository,
            SongRatingRepository songRatingRepository,
            AlbumReviewRepository albumReviewRepository,
            AlbumListRepository albumListRepository,
            AlbumFavoriteRepository albumFavoriteRepository,
            UserFollowRepository userFollowRepository) {

        this.genreRepository = genreRepository;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.songRepository = songRepository;
        this.artistGenreRepository = artistGenreRepository;
        this.artistSongRepository = artistSongRepository;
        this.userRepository = userRepository;
        this.albumRatingRepository = albumRatingRepository;
        this.songRatingRepository = songRatingRepository;
        this.albumReviewRepository = albumReviewRepository;
        this.albumListRepository = albumListRepository;
        this.albumFavoriteRepository = albumFavoriteRepository;
        this.userFollowRepository = userFollowRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        if (userRepository.count() > 0) {
            return;
        }

        seedGenres();
        seedCatalog();
        seedUsers();
        seedActivity();
    }

    private void seedGenres() {

        for (String name : new String[]{
                "Rock", "Pop", "Hip-Hop", "Electronic",
                "Jazz", "Alternative", "R&B", "Metal"}) {

            Genre genre = new Genre();
            genre.setName(name);
            genres.put(name, genreRepository.save(genre));
        }
    }

    private void seedCatalog() {

        Artist radiohead = artist("Radiohead",
                "English rock band formed in Abingdon in 1985.",
                "Alternative", "Rock");
        album(radiohead, "OK Computer", LocalDate.of(1997, 5, 21),
                new String[]{"Alternative", "Rock"},
                "Airbag", 284, "Paranoid Android", 383,
                "Karma Police", 261, "No Surprises", 229);
        album(radiohead, "In Rainbows", LocalDate.of(2007, 10, 10),
                new String[]{"Alternative"},
                "15 Step", 237, "Nude", 255, "Reckoner", 290);

        Artist pinkFloyd = artist("Pink Floyd",
                "English progressive rock band formed in London in 1965.",
                "Rock");
        album(pinkFloyd, "The Dark Side of the Moon", LocalDate.of(1973, 3, 1),
                new String[]{"Rock"},
                "Time", 413, "Money", 382, "Us and Them", 469);

        Artist kendrick = artist("Kendrick Lamar",
                "American rapper and songwriter from Compton, California.",
                "Hip-Hop");
        album(kendrick, "To Pimp a Butterfly", LocalDate.of(2015, 3, 15),
                new String[]{"Hip-Hop", "Jazz"},
                "King Kunta", 234, "Alright", 219, "The Blacker the Berry", 328);
        album(kendrick, "good kid, m.A.A.d city", LocalDate.of(2012, 10, 22),
                new String[]{"Hip-Hop"},
                "Money Trees", 386, "Swimming Pools", 313);

        Artist daftPunk = artist("Daft Punk",
                "French electronic music duo formed in Paris in 1993.",
                "Electronic");
        album(daftPunk, "Random Access Memories", LocalDate.of(2013, 5, 17),
                new String[]{"Electronic", "Pop"},
                "Get Lucky", 369, "Instant Crush", 337, "Giorgio by Moroder", 544);
        album(daftPunk, "Discovery", LocalDate.of(2001, 3, 12),
                new String[]{"Electronic"},
                "One More Time", 320, "Digital Love", 301);

        Artist milesDavis = artist("Miles Davis",
                "American jazz trumpeter, bandleader and composer.",
                "Jazz");
        album(milesDavis, "Kind of Blue", LocalDate.of(1959, 8, 17),
                new String[]{"Jazz"},
                "So What", 562, "Freddie Freeloader", 586, "Blue in Green", 337);

        Artist frankOcean = artist("Frank Ocean",
                "American singer, songwriter and record producer.",
                "R&B");
        album(frankOcean, "Blonde", LocalDate.of(2016, 8, 20),
                new String[]{"R&B", "Alternative"},
                "Nikes", 314, "Ivy", 249, "Pink + White", 184);
        album(frankOcean, "channel ORANGE", LocalDate.of(2012, 7, 10),
                new String[]{"R&B"},
                "Thinkin Bout You", 200, "Pyramids", 592);

        Artist metallica = artist("Metallica",
                "American heavy metal band formed in Los Angeles in 1981.",
                "Metal");
        album(metallica, "Master of Puppets", LocalDate.of(1986, 3, 3),
                new String[]{"Metal"},
                "Battery", 312, "Master of Puppets", 515);

        Artist beatles = artist("The Beatles",
                "English rock band formed in Liverpool in 1960.",
                "Rock", "Pop");
        album(beatles, "Abbey Road", LocalDate.of(1969, 9, 26),
                new String[]{"Rock", "Pop"},
                "Come Together", 259, "Something", 182, "Here Comes the Sun", 185);

        Artist arcticMonkeys = artist("Arctic Monkeys",
                "English rock band formed in Sheffield in 2002.",
                "Alternative", "Rock");
        album(arcticMonkeys, "AM", LocalDate.of(2013, 9, 9),
                new String[]{"Alternative", "Rock"},
                "Do I Wanna Know?", 272, "R U Mine?", 201, "Arabella", 207);

        Artist billie = artist("Billie Eilish",
                "American singer-songwriter from Los Angeles.",
                "Pop");
        album(billie, "When We All Fall Asleep, Where Do We Go?", LocalDate.of(2019, 3, 29),
                new String[]{"Pop", "Electronic"},
                "bad guy", 194, "bury a friend", 193);
    }

    private void seedUsers() {

        user("rokas", "rokas@example.com", "Rokas");
        user("egle", "egle@example.com", "Eglė");
        user("jonas", "jonas@example.com", "Jonas");
        user("ieva", "ieva@example.com", "Ieva");
        user("lukas", "lukas@example.com", "Lukas");
    }

    private void seedActivity() {

        albumRating("rokas", "OK Computer", 10);
        albumRating("rokas", "In Rainbows", 9);
        albumRating("rokas", "AM", 8);
        albumRating("rokas", "Kind of Blue", 9);
        albumRating("egle", "Blonde", 10);
        albumRating("egle", "channel ORANGE", 9);
        albumRating("egle", "OK Computer", 8);
        albumRating("egle", "When We All Fall Asleep, Where Do We Go?", 7);
        albumRating("jonas", "To Pimp a Butterfly", 10);
        albumRating("jonas", "good kid, m.A.A.d city", 9);
        albumRating("jonas", "Random Access Memories", 8);
        albumRating("ieva", "Abbey Road", 10);
        albumRating("ieva", "The Dark Side of the Moon", 9);
        albumRating("ieva", "Discovery", 8);
        albumRating("lukas", "Master of Puppets", 10);
        albumRating("lukas", "The Dark Side of the Moon", 8);
        albumRating("lukas", "AM", 6);

        songRating("rokas", "Paranoid Android", 10);
        songRating("rokas", "Reckoner", 9);
        songRating("egle", "Ivy", 10);
        songRating("egle", "Pyramids", 9);
        songRating("jonas", "Alright", 10);
        songRating("ieva", "Here Comes the Sun", 10);
        songRating("lukas", "Battery", 9);

        review("rokas", "OK Computer", "A timeless masterpiece",
                "Every track flows into the next. Paranoid Android is still "
                        + "one of the most ambitious rock songs ever written.");
        review("egle", "Blonde", "Quiet and intimate",
                "Minimal production that leaves space for Frank's voice. "
                        + "Ivy and Nikes are highlights.");
        review("jonas", "To Pimp a Butterfly", "Jazz meets hip-hop",
                "A dense, political record with incredible live "
                        + "instrumentation. Needs several listens.");
        review("ieva", "Abbey Road", "The perfect closing chapter",
                "The medley on side two is the best thing the Beatles did.");
        review("lukas", "Master of Puppets", "Thrash metal at its peak",
                "Fast, heavy and surprisingly melodic. Battery opens the album "
                        + "perfectly.");

        list("rokas", "Best of the 90s and 2000s",
                "Albums that defined my teenage years",
                "OK Computer", "In Rainbows", "Discovery");
        list("egle", "Late night listening",
                "Calm albums for the evening",
                "Blonde", "channel ORANGE", "Kind of Blue");
        list("jonas", "Hip-hop essentials", null,
                "To Pimp a Butterfly", "good kid, m.A.A.d city");

        favorite("rokas", "OK Computer");
        favorite("rokas", "Kind of Blue");
        favorite("egle", "Blonde");
        favorite("jonas", "To Pimp a Butterfly");
        favorite("ieva", "Abbey Road");
        favorite("lukas", "Master of Puppets");

        follow("rokas", "egle");
        follow("rokas", "jonas");
        follow("egle", "rokas");
        follow("jonas", "ieva");
        follow("ieva", "lukas");
        follow("lukas", "rokas");
    }

    private Artist artist(String name, String biography, String... genreNames) {

        Artist artist = new Artist();
        artist.setName(name);
        artist.setBiography(biography);
        artist = artistRepository.save(artist);

        for (String genreName : genreNames) {
            ArtistGenre artistGenre = new ArtistGenre();
            artistGenre.setArtist(artist);
            artistGenre.setGenre(genres.get(genreName));
            artistGenreRepository.save(artistGenre);
        }

        return artist;
    }

    /**
     * Tracks are given as alternating title and duration in seconds.
     */
    private void album(
            Artist artist,
            String title,
            LocalDate releaseDate,
            String[] genreNames,
            Object... tracks) {

        Album album = new Album();
        album.setTitle(title);
        album.setReleaseDate(releaseDate);
        album.getArtists().add(artist);

        for (String genreName : genreNames) {
            AlbumGenre albumGenre = new AlbumGenre();
            albumGenre.setAlbum(album);
            albumGenre.setGenre(genres.get(genreName));
            album.getGenres().add(albumGenre);
        }

        for (int i = 0; i < tracks.length; i += 2) {
            Song song = new Song();
            song.setTitle((String) tracks[i]);
            song.setDurationSeconds((Integer) tracks[i + 1]);
            song.setOriginalReleaseDate(releaseDate);

            for (String genreName : genreNames) {
                SongGenre songGenre = new SongGenre();
                songGenre.setSong(song);
                songGenre.setGenre(genres.get(genreName));
                song.getGenres().add(songGenre);
            }

            song = songRepository.save(song);
            songs.put(song.getTitle(), song);

            SongArtist songArtist = new SongArtist();
            songArtist.setSong(song);
            songArtist.setArtist(artist);
            artistSongRepository.save(songArtist);

            AlbumSong albumSong = new AlbumSong();
            albumSong.setAlbum(album);
            albumSong.setSong(song);
            albumSong.setTrackNumber(i / 2 + 1);
            albumSong.setDiscNumber(1);
            album.getSongs().add(albumSong);
        }

        albums.put(title, albumRepository.save(album));
    }

    private void user(String username, String email, String displayName) {

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password123");
        user.setDisplayName(displayName);
        users.put(username, userRepository.save(user));
    }

    private void albumRating(String username, String albumTitle, int value) {

        AlbumRating rating = new AlbumRating();
        rating.setUser(users.get(username));
        rating.setAlbum(albums.get(albumTitle));
        rating.setRating(value);
        albumRatingRepository.save(rating);
    }

    private void songRating(String username, String songTitle, int value) {

        SongRating rating = new SongRating();
        rating.setUser(users.get(username));
        rating.setSong(songs.get(songTitle));
        rating.setRating(value);
        songRatingRepository.save(rating);
    }

    private void review(String username, String albumTitle, String title, String content) {

        AlbumReview review = new AlbumReview();
        review.setUser(users.get(username));
        review.setAlbum(albums.get(albumTitle));
        review.setTitle(title);
        review.setContent(content);
        albumReviewRepository.save(review);
    }

    private void list(String username, String name, String description, String... albumTitles) {

        AlbumList albumList = new AlbumList();
        albumList.setUser(users.get(username));
        albumList.setName(name);
        albumList.setDescription(description);

        for (int i = 0; i < albumTitles.length; i++) {
            AlbumListItem item = new AlbumListItem();
            item.setAlbumList(albumList);
            item.setAlbum(albums.get(albumTitles[i]));
            item.setPosition(i + 1);
            albumList.getAlbums().add(item);
        }

        albumListRepository.save(albumList);
    }

    private void favorite(String username, String albumTitle) {

        AlbumFavorite favorite = new AlbumFavorite();
        favorite.setUser(users.get(username));
        favorite.setAlbum(albums.get(albumTitle));
        albumFavoriteRepository.save(favorite);
    }

    private void follow(String follower, String followed) {

        UserFollow follow = new UserFollow();
        follow.setFollower(users.get(follower));
        follow.setFollowed(users.get(followed));
        userFollowRepository.save(follow);
    }
}
