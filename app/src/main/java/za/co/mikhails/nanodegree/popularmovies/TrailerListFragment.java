package za.co.mikhails.nanodegree.popularmovies;

import android.app.Fragment;
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

import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract.TrailersEntry;
import za.co.mikhails.nanodegree.popularmovies.sync.ThemoviedbSyncAdapter;

public class TrailerListFragment extends Fragment implements android.app.LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {
    public static final String MOVIE_ID = "movie_id";

    private static final int TRAILER_LOADER = 0;

    private static final String[] COLUMNS = {
            TrailersEntry.TABLE_NAME + "." + TrailersEntry._ID,
            TrailersEntry.COLUMN_TRAILER_ID,
            TrailersEntry.COLUMN_MOVIE_ID,
            TrailersEntry.COLUMN_KEY,
            TrailersEntry.COLUMN_NAME,
            TrailersEntry.COLUMN_SITE,
            TrailersEntry.COLUMN_SIZE,
            TrailersEntry.COLUMN_TYPE
    };

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_TRAILER_ID = 1;
    public static final int COLUMN_MOVIE_ID = 2;
    public static final int COLUMN_KEY = 3;
    public static final int COLUMN_NAME = 4;
    public static final int COLUMN_SITE = 5;
    public static final int COLUMN_SIZE = 6;
    public static final int COLUMN_TYPE = 7;

    public static final String SORT_ORDER_TRAILERS = TrailersEntry.COLUMN_TYPE + " DESC," + TrailersEntry.COLUMN_TRAILER_ID + " ASC";

    private int mMovieId = -1;
    private TrailerListAdapter mTrailerListAdapter;
    private ListView mListView;

    public TrailerListFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_trailer_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listview);
        mTrailerListAdapter = new TrailerListAdapter(getActivity(), null, 0);
        mListView.setAdapter(mTrailerListAdapter);
        mListView.setOnItemClickListener(this);

        ThemoviedbSyncAdapter.syncTrailersListImmediately(getActivity(), Integer.toString(mMovieId));

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String key = (String) view.getTag(R.id.TRAILER_KEY);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MessageFormat.format(getString(R.string.youtube_watch_url), key))));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = new Bundle();
        bundle.putInt(MovieDetailFragment.MOVIE_ID, mMovieId);
        getLoaderManager().initLoader(TRAILER_LOADER, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri provider = TrailersEntry.CONTENT_URI.buildUpon().appendPath("*").build();
        return new CursorLoader(getActivity(), provider, COLUMNS, TrailersEntry.COLUMN_MOVIE_ID + " = " + mMovieId, null, SORT_ORDER_TRAILERS);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTrailerListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTrailerListAdapter.swapCursor(null);
    }
}
