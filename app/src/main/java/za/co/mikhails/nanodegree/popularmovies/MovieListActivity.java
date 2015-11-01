package za.co.mikhails.nanodegree.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract;
import za.co.mikhails.nanodegree.popularmovies.sync.SyncAdapterMovies;

public class MovieListActivity extends AppCompatActivity implements MovieListFragment.Callbacks {
    public static final String PREFS_NAME = "MoviesPrefsFile";

    public static final String SORT_ORDER_POPULARITY = MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC," + MoviesContract.MoviesEntry._ID + " ASC";
    public static final String SORT_ORDER_RATING = MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC," + MoviesContract.MoviesEntry._ID + " ASC";
    public static final String SORT_ORDER_DEFAULT = SORT_ORDER_POPULARITY;
    public static final String SORT_ORDER = "sortorder";

    private boolean mTwoPane;
    private MenuItem mItemPopular;
    private MenuItem mItemRating;
    private String mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        MovieListFragment movieListFragment = (MovieListFragment) getFragmentManager().findFragmentById(R.id.movie_list);
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            movieListFragment.setActivateOnItemClick(true);
        }

        SyncAdapterMovies.initializeSyncAdapter(this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mSortOrder = settings.getString(SORT_ORDER, SORT_ORDER_DEFAULT);
        switch (mSortOrder) {
            case SORT_ORDER_POPULARITY:
                movieListFragment.setSortOrder(SORT_ORDER_POPULARITY);
                break;
            case SORT_ORDER_RATING:
                movieListFragment.setSortOrder(SORT_ORDER_RATING);
                break;
        }
    }

    @Override
    public void onItemSelected(int id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(MovieDetailFragment.MOVIE_ID, id);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction().replace(R.id.movie_detail_container, fragment).commit();
        } else {
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtra(MovieDetailFragment.MOVIE_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        switch (mSortOrder) {
            case SORT_ORDER_POPULARITY:
                mItemPopular = menu.findItem(R.id.action_sort_by_popular);
                mItemPopular.setChecked(true);
                break;
            case SORT_ORDER_RATING:
                mItemRating = menu.findItem(R.id.action_sort_by_rating);
                mItemRating.setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_popular:
                item.setChecked(true);
                updateSortOrder(SORT_ORDER_POPULARITY);
                return true;
            case R.id.action_sort_by_rating:
                item.setChecked(true);
                updateSortOrder(SORT_ORDER_RATING);
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSortOrder(String sortOrder) {
        mSortOrder = sortOrder;
        ((MovieListFragment) getFragmentManager().findFragmentById(R.id.movie_list)).setSortOrder(sortOrder);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        settings.edit().putString(SORT_ORDER, mSortOrder).commit();
    }
}
