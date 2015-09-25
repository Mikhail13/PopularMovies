package za.co.mikhails.nanodegree.popularmovies;

import android.app.Fragment;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract.MoviesEntry;

public class MovieDetailFragment extends Fragment implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    public static final String MOVIE_ID = "movie_id";

    private static final int DETAIL_LOADER = 0;

    private static final String[] COLUMNS = {
            MoviesEntry.TABLE_NAME + "." + MoviesEntry._ID,
            MoviesEntry.COLUMN_MOVIE_ID,
            MoviesEntry.COLUMN_ORIGINAL_TITLE,
            MoviesEntry.COLUMN_OVERVIEW,
            MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesEntry.COLUMN_POSTER_PATH,
            MoviesEntry.COLUMN_POPULARITY,
            MoviesEntry.COLUMN_VOTE_AVERAGE
    };

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_MOVIE_ID = 1;
    private static final int COLUMN_ORIGINAL_TITLE = 2;
    private static final int COLUMN_OVERVIEW = 3;
    private static final int COLUMN_RELEASE_DATE = 4;
    private static final int COLUMN_POSTER_PATH = 5;
    private static final int COLUMN_POPULARITY = 6;
    private static final int COLUMN_VOTE_AVERAGE = 7;

    private int mMovieId = -1;
    private TextView mOverviewView;
    private ImageView mPosterView;
    private TextView mOriginalTitle;
    private TextView mReleaseDate;
    private TextView mVoteAverage;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MOVIE_ID)) {
            mMovieId = getArguments().getInt(MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mOverviewView = (TextView) rootView.findViewById(R.id.overview);
        mOriginalTitle = (TextView) rootView.findViewById(R.id.original_title);
        mPosterView = (ImageView) rootView.findViewById(R.id.poster);
        mReleaseDate = (TextView) rootView.findViewById(R.id.release_date);
        mVoteAverage = (TextView) rootView.findViewById(R.id.vote_average);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = "(" + MoviesEntry.COLUMN_MOVIE_ID + " = '" + mMovieId + "')";
        Uri provider = MoviesEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mMovieId)).build();
        return new CursorLoader(getActivity(), provider, COLUMNS, selection, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String title = data.getString(COLUMN_ORIGINAL_TITLE);
            mOriginalTitle.setText(title);

            String overview = data.getString(COLUMN_OVERVIEW);
            mOverviewView.setText(overview);

            String posterPath = data.getString(COLUMN_POSTER_PATH);
            if (posterPath != null) {
                Picasso.with(getActivity()).load(getActivity().getString(R.string.tmdb_poster_url) + "/" + posterPath).into(mPosterView);
            } else {
                mPosterView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_movie));
            }

            String releaseDate = data.getString(COLUMN_RELEASE_DATE);
            mReleaseDate.setText(Utils.formatReleaseDate(getActivity(), releaseDate));

            String voteAverage = data.getString(COLUMN_VOTE_AVERAGE);
            mVoteAverage.setText(Utils.formatVoteAverage(getActivity(), voteAverage));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
