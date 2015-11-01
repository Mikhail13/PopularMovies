package za.co.mikhails.nanodegree.popularmovies;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.MessageFormat;

import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract;
import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract.MoviesEntry;
import za.co.mikhails.nanodegree.popularmovies.sync.SyncAdapterMovies;

public class MovieDetailFragment extends Fragment implements AdapterView.OnItemClickListener,
        android.app.LoaderManager.LoaderCallbacks<Cursor> {

    public static final String MOVIE_ID = "movie_id";
    private static final int DETAIL_LOADER = 0;

    private int mMovieId = -1;

    private ListView mListView;
    private CursorLoader mDetailLoader;
    private MovieDetailAdapter mMovieDetailAdapter;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MOVIE_ID)) {
            mMovieId = getArguments().getInt(MOVIE_ID);
        }
        SyncAdapterMovies.syncMovieDetailsImmediately(getActivity(), Integer.toString(mMovieId));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview);

        mMovieDetailAdapter = new MovieDetailAdapter(getActivity(), null, 0);
        mListView.setAdapter(mMovieDetailAdapter);
        mListView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_ID, mMovieId);
        getLoaderManager().initLoader(DETAIL_LOADER, bundle, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String key = (String) view.getTag(R.id.TRAILER_KEY);
        if (key != null && !key.isEmpty()) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MessageFormat.format(getString(R.string.youtube_watch_url), key))));
        } else {
            String url = (String) view.getTag(R.id.REVIEW_URL);
            if (url != null && !url.isEmpty()) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        }
    }

    // **********  Loader Callbacks  **********

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id == DETAIL_LOADER && bundle.containsKey(MOVIE_ID)) {
            int movieId = bundle.getInt(MOVIE_ID);
            Uri.Builder builder = MoviesContract.BASE_CONTENT_URI.buildUpon()
                    .appendPath(MoviesContract.PATH_MOVIES)
                    .appendPath(MoviesContract.PATH_MOVIE_DETAILS);
            ContentUris.appendId(builder, movieId);
            mDetailLoader = new CursorLoader(getActivity(), builder.build(), null, null, null, null);
            return mDetailLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader == mDetailLoader) {
            mMovieDetailAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader == mDetailLoader) {
            mMovieDetailAdapter.swapCursor(null);
        }
    }
}
