package za.co.mikhails.nanodegree.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.MessageFormat;

import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract.MoviesEntry;
import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract.ReviewsEntry;
import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract.TrailersEntry;

public class MovieDetailActivity extends AppCompatActivity {

    private int mMovieId = -1;
    private boolean mFavorites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            mMovieId = getIntent().getIntExtra(MovieDetailFragment.MOVIE_ID, -1);
            mFavorites = getIntent().getExtras().containsKey(MovieDetailFragment.FAVORITES);
            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction().
                    add(R.id.movie_detail_container, movieDetailFragment).
                    commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mMovieId != -1) {
            getMenuInflater().inflate(R.menu.menu_details, menu);
            if (mFavorites) {
                menu.removeItem(R.id.action_add_to_favorites);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_to_favorites:
                String title = copyMovieToFavorites(mMovieId);
                if (title != null) {
                    String toastText = MessageFormat.format(getString(R.string.toast_add_to_favorites), title);
                    Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_share_trailer:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String copyMovieToFavorites(int movieId) {
        String title = null;

        String[] projection = new String[]{"*"};
        String selection = MoviesEntry.COLUMN_MOVIE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(movieId)};

        Cursor cursor = getContentResolver().query(MoviesEntry.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor.moveToFirst()) {
            ContentValues contentValues = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
            contentValues.remove("_id");
            title = contentValues.getAsString(MoviesEntry.COLUMN_ORIGINAL_TITLE);
            getContentResolver().insert(MoviesEntry.FAVORITES_CONTENT_URI, contentValues);
        }

        cursor = getContentResolver().query(TrailersEntry.CONTENT_URI, projection, selection, selectionArgs, null);
        while (cursor.moveToNext()) {
            ContentValues contentValues = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
            contentValues.remove("_id");
            getContentResolver().insert(TrailersEntry.FAVORITES_CONTENT_URI, contentValues);
        }

        cursor = getContentResolver().query(ReviewsEntry.CONTENT_URI, projection, selection, selectionArgs, null);
        while (cursor.moveToNext()) {
            ContentValues contentValues = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
            contentValues.remove("_id");
            getContentResolver().insert(ReviewsEntry.FAVORITES_CONTENT_URI, contentValues);
        }

        return title;
    }

    private void shareTrailerUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
        share.putExtra(Intent.EXTRA_TEXT, "https://www.codeofaninja.com");
        startActivity(Intent.createChooser(share, "Share link!"));
    }
}
