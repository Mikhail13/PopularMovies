package za.co.mikhails.nanodegree.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "za.co.mikhails.nanodegree.popularmovies.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_TRAILERS = "trailers";

    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        // Table name
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TrailersEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

        // Table name
        public static final String TABLE_NAME = "trailers";

        public static final String COLUMN_TRAILER_ID = "id";
        public static final String COLUMN_ISO_639_1 = "original_title";
        public static final String COLUMN_KEY = "overview";
        public static final String COLUMN_NAME = "release_date";
        public static final String COLUMN_SITE = "poster_path";
        public static final String COLUMN_SIZE = "popularity";
        public static final String COLUMN_TYPE = "vote_average";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
