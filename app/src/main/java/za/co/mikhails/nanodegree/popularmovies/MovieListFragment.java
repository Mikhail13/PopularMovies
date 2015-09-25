package za.co.mikhails.nanodegree.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract.MoviesEntry;
import za.co.mikhails.nanodegree.popularmovies.sync.ThemoviedbSyncAdapter;

public class MovieListFragment extends Fragment implements android.app.LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private static final int MOVIE_LOADER = 0;

    private static final String[] COLUMNS = {
            MoviesEntry.TABLE_NAME + "." + MoviesEntry._ID,
            MoviesEntry.COLUMN_MOVIE_ID,
            MoviesEntry.COLUMN_POSTER_PATH
    };

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_MOVIE_ID = 1;
    public static final int COLUMN_POSTER_PATH = 2;

    private static final String STATE_GRID_POSITION = "state_grid_position";
    private static final String STATE_GRID_VIEW = "state_grid_view";

    private String mSortOrder;
    private Callbacks mCallbacks = sDummyCallbacks;
    private MovieListAdapter mMovieListAdapter;
    private GridView mGridView;
    private int mRestorePosition = -1;
    private Parcelable mRestoreGridViewState;

    public interface Callbacks {
        void onItemSelected(int id);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int id) {
        }
    };

    public MovieListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mMovieListAdapter = new MovieListAdapter(getActivity(), null, 0);
        mGridView.setAdapter(mMovieListAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(this);


        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_GRID_POSITION)) {
            mRestorePosition = savedInstanceState.getInt(STATE_GRID_POSITION);
            mRestoreGridViewState = savedInstanceState.getParcelable(STATE_GRID_VIEW);
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCallbacks.onItemSelected((Integer) view.getTag(R.id.MOVIE_ID));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        int position = mGridView.getFirstVisiblePosition();
        outState.putInt(STATE_GRID_POSITION, position);
        outState.putParcelable(STATE_GRID_VIEW, mGridView.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    public void setSortOrder(String sortOrder) {
        mSortOrder = sortOrder;

        Bundle bundle = new Bundle();
        bundle.putString(MovieListActivity.SORT_ORDER, sortOrder);
        getLoaderManager().restartLoader(MOVIE_LOADER, bundle, this);

        ThemoviedbSyncAdapter.syncImmediately(getActivity(), sortOrder.equals(MovieListActivity.SORT_ORDER_POPULARITY) ?
                ThemoviedbSyncAdapter.SORT_BY_POPULAR :
                ThemoviedbSyncAdapter.SORT_BY_RATING);

        mMovieListAdapter.notifyDataSetChanged();
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        mGridView.setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = new Bundle();
        if (mSortOrder != null) {
            bundle.putString(MovieListActivity.SORT_ORDER, mSortOrder);
        }
        getLoaderManager().initLoader(MOVIE_LOADER, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri provider = MoviesEntry.CONTENT_URI.buildUpon().appendPath("*").build();
        return new CursorLoader(getActivity(), provider, COLUMNS, null, null, bundle.getString(MovieListActivity.SORT_ORDER, MovieListActivity.SORT_ORDER_DEFAULT));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieListAdapter.swapCursor(data);

        if (mRestorePosition != -1 && mRestoreGridViewState != null && mGridView.getCount() >= mRestorePosition) {
            mGridView.onRestoreInstanceState(mRestoreGridViewState);
            mRestorePosition = -1;
            mRestoreGridViewState = null;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieListAdapter.swapCursor(null);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount - visibleItemCount < firstVisibleItem + visibleItemCount * 2) {
            ThemoviedbSyncAdapter.loadNextPageOnScroll(MovieListFragment.this.getActivity());
        }
    }
}
