package za.co.mikhails.nanodegree.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract.MoviesEntry;
import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract.TrailersEntry;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY," +
                MoviesEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MoviesEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                MoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MoviesEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MoviesEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);

        final String SQL_CREATE_TRAILERSS_TABLE = "CREATE TABLE " + TrailersEntry.TABLE_NAME + " (" +
                MoviesContract.TrailersEntry._ID + " INTEGER PRIMARY KEY," +
                MoviesContract.TrailersEntry.COLUMN_TRAILER_ID + " INTEGER UNIQUE NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_ISO_639_1 + " TEXT, " +
                MoviesContract.TrailersEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_NAME + " TEXT, " +
                MoviesContract.TrailersEntry.COLUMN_SITE + " TEXT, " +
                MoviesContract.TrailersEntry.COLUMN_SIZE + " TEXT, " +
                MoviesContract.TrailersEntry.COLUMN_TYPE + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERSS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailersEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
