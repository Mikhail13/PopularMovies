package za.co.mikhails.nanodegree.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(MovieDetailFragment.MOVIE_ID, getIntent().getIntExtra(MovieDetailFragment.MOVIE_ID, -1));
            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(arguments);

            arguments = new Bundle();
            arguments.putInt(MovieDetailFragment.MOVIE_ID, getIntent().getIntExtra(MovieDetailFragment.MOVIE_ID, -1));
            TrailerListFragment trailerListFragment = new TrailerListFragment();
            trailerListFragment.setArguments(arguments);

            getFragmentManager().beginTransaction().
                    add(R.id.movie_detail_container, movieDetailFragment).
                    add(R.id.movie_detail_container, trailerListFragment).
                    commit();
        }
    }
}
