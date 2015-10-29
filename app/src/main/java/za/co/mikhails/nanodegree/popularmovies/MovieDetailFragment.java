package za.co.mikhails.nanodegree.popularmovies;

import android.app.Fragment;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.MessageFormat;

import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract.MoviesEntry;
import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract.TrailersEntry;
import za.co.mikhails.nanodegree.popularmovies.sync.ThemoviedbSyncAdapter;

public class MovieDetailFragment extends Fragment implements AdapterView.OnItemClickListener,
        android.app.LoaderManager.LoaderCallbacks<Cursor> {

    public static final String MOVIE_ID = "movie_id";
    private static final int DETAIL_LOADER = 0;
    private static final int TRAILER_LOADER = 1;

    private static final String[] DETAIL_COLUMNS = {
            MoviesEntry.TABLE_NAME + "." + MoviesEntry._ID,
            MoviesEntry.COLUMN_ORIGINAL_TITLE,
            MoviesEntry.COLUMN_OVERVIEW,
            MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesEntry.COLUMN_POSTER_PATH,
            MoviesEntry.COLUMN_VOTE_AVERAGE
    };

    private static final int DETAIL_COLUMN_ORIGINAL_TITLE = 1;
    private static final int DETAIL_COLUMN_OVERVIEW = 2;
    private static final int DETAIL_COLUMN_RELEASE_DATE = 3;
    private static final int DETAIL_COLUMN_POSTER_PATH = 4;
    private static final int DETAIL_COLUMN_VOTE_AVERAGE = 5;

    private static final String[] TRAILER_COLUMNS = {
            TrailersEntry.TABLE_NAME + "." + TrailersEntry._ID,
            TrailersEntry.COLUMN_KEY,
            TrailersEntry.COLUMN_NAME,
            TrailersEntry.COLUMN_SIZE,
            TrailersEntry.COLUMN_TYPE
    };

    public static final int TRAILER_COLUMN_KEY = 1;
    public static final int TRAILER_COLUMN_NAME = 2;
    public static final int TRAILER_COLUMN_SIZE = 3;
    public static final int TRAILER_COLUMN_TYPE = 4;

    public static final String SORT_ORDER_TRAILERS = TrailersEntry.COLUMN_TYPE + " DESC," + TrailersEntry.COLUMN_TRAILER_ID + " ASC";

    private int mMovieId = -1;

    private TextView mOverviewView;
    private ImageView mPosterView;
    private TextView mOriginalTitle;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private ListView mListView;
    private CursorLoader detailLoader;
    private CursorLoader trailerLoader;
    private TrailerListAdapter mTrailerListAdapter;

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
        mListView = (ListView) rootView.findViewById(R.id.listview);

        mTrailerListAdapter = new TrailerListAdapter(getActivity(), null, 0);
        mListView.setAdapter(mTrailerListAdapter);
        mListView.setOnItemClickListener(this);

        ThemoviedbSyncAdapter.syncTrailersListImmediately(getActivity(), Integer.toString(mMovieId));

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String key = (String) view.getTag(R.id.TRAILER_KEY);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MessageFormat.format(getString(R.string.youtube_watch_url), key))));
    }

    private void resizeListView() {
        int combinedChildrenHeight = 0;
        int count = mTrailerListAdapter.getCount();
        for (int i = 0; i < count; i++) {
            View view = mTrailerListAdapter.getView(i, null, mListView);
            view.measure(0, 0);
            combinedChildrenHeight += view.getMeasuredHeight();
        }
        combinedChildrenHeight += mListView.getDividerHeight() * (count - 1);
        ViewGroup.LayoutParams layoutParams = mListView.getLayoutParams();
        layoutParams.height = combinedChildrenHeight;
        mListView.setLayoutParams(layoutParams);
        mListView.setMinimumHeight(combinedChildrenHeight);
    }

    // **********  Loader Callbacks  **********

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == DETAIL_LOADER) {
            String selection = "(" + MoviesEntry.COLUMN_MOVIE_ID + " = '" + mMovieId + "')";
            Uri provider = MoviesEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mMovieId)).build();
            detailLoader = new CursorLoader(getActivity(), provider, DETAIL_COLUMNS, selection, null, null);
            return detailLoader;
        } else if (id == TRAILER_LOADER) {
            Uri provider = TrailersEntry.CONTENT_URI.buildUpon().appendPath("*").build();
            trailerLoader = new CursorLoader(getActivity(), provider, TRAILER_COLUMNS, TrailersEntry.COLUMN_MOVIE_ID + " = " + mMovieId, null, SORT_ORDER_TRAILERS);
            return trailerLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader == detailLoader) {
            if (data != null && data.moveToFirst()) {
                String title = data.getString(DETAIL_COLUMN_ORIGINAL_TITLE);
                mOriginalTitle.setText(title);

                String overview = data.getString(DETAIL_COLUMN_OVERVIEW);
                mOverviewView.setText(overview);

                String posterPath = data.getString(DETAIL_COLUMN_POSTER_PATH);
                if (posterPath != null) {
                    Picasso.with(getActivity()).load(getActivity().getString(R.string.tmdb_poster_url) + "/" + posterPath).into(mPosterView);
                } else {
                    mPosterView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_movie));
                }

                String releaseDate = data.getString(DETAIL_COLUMN_RELEASE_DATE);
                mReleaseDate.setText(Utils.formatReleaseDate(getActivity(), releaseDate));

                String voteAverage = data.getString(DETAIL_COLUMN_VOTE_AVERAGE);
                mVoteAverage.setText(Utils.formatVoteAverage(getActivity(), voteAverage));
            }
        } else if (loader == trailerLoader) {
            mTrailerListAdapter.swapCursor(data);
            resizeListView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader == trailerLoader) {
            mTrailerListAdapter.swapCursor(null);
        }
    }
}
