package za.co.mikhails.nanodegree.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract.MoviesEntry;
import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract.TrailersEntry;

public class MoviesProvider extends ContentProvider {
    private MoviesDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int MOVIE = 100;
    static final int MOVIES_LIST = 101;
    static final int TRAILERS_LIST = 102;

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES_LIST:
                retCursor = mOpenHelper.getReadableDatabase().query(MoviesEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE:
                retCursor = mOpenHelper.getReadableDatabase().query(MoviesEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case TRAILERS_LIST:
                retCursor = mOpenHelper.getReadableDatabase().query(TrailersEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MOVIES_LIST:
                return MoviesEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MoviesEntry.CONTENT_TYPE;
            case TRAILERS_LIST:
                return TrailersEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE:
            case MOVIES_LIST:
                long movieId = db.insert(MoviesEntry.TABLE_NAME, null, values);
                if (movieId > 0) {
                    returnUri = MoviesEntry.buildMovieUri(movieId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case TRAILERS_LIST:
                long trailerId = db.insert(TrailersEntry.TABLE_NAME, null, values);
                if (trailerId > 0) {
                    returnUri = TrailersEntry.buildTrailerUri(trailerId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE:
            case MOVIES_LIST:
                rowsDeleted = db.delete(MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILERS_LIST:
                rowsDeleted = db.delete(TrailersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MoviesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRAILERS_LIST:
                rowsUpdated = db.update(TrailersEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnMoviesCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MoviesEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnMoviesCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnMoviesCount;
            case TRAILERS_LIST:
                db.beginTransaction();
                int returnTrailersCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(TrailersEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnTrailersCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnTrailersCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/*", MOVIES_LIST);
        matcher.addURI(authority, MoviesContract.PATH_TRAILERS + "/*", TRAILERS_LIST);
        return matcher;
    }
}
