package com.example.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnMovieItemClickListener {

    private static final String IS_MOST_POPULAR = "is_most_popular";
    private static final String PAGE_COUNT = "page_count";
    private static final String MOVIES = "movies";

    private Integer page = 1;
    private Boolean isMostPopular = true;
    private Boolean isLoading = false;

    @BindView(R.id.pb_rv)
    private ProgressBar progressBar;
    @BindView(R.id.tv_error_message)
    private TextView errorMessageTV;
    @BindView(R.id.rv_movies)
    private RecyclerView recyclerView;
    @BindView(R.id.frameLayout)
    private FrameLayout frameLayout;

    private PopularMoviesAdapter adapter;
    private Snackbar snackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new PopularMoviesAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), getSpanCount());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        if (savedInstanceState != null) {
            isMostPopular = savedInstanceState.getBoolean(IS_MOST_POPULAR);
            setTitle(isMostPopular ? "Popular Movies" : "Rated Movies");
            page = savedInstanceState.getInt(PAGE_COUNT);
            adapter.setMovies(savedInstanceState.<Movie>getParcelableArrayList(MOVIES));
        } else {
            loadMovies(isMostPopular);
        }

        recyclerView.addOnScrollListener(new PagingScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                page++;
                loadMovies(isMostPopular);
            }

            @Override
            public boolean isLoading() { return isLoading; }
        });
    }


    int getPageNumber() {
        return page;
    }

    void updateRecyclerView(List<Movie> movies) {
        hideProgressBar();

        if (movies.isEmpty() && page == 1) {
            showErrorMessage();
        } else {
            hideErrorMessage();
            if (page == 1) {
                adapter.setMovies(movies);
            } else {
                isLoading = false;
                adapter.addAll(movies);
            }
        }
    }

    private int getSpanCount() {
        int spanCount = 2;

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        boolean isLandscape = getResources().getBoolean(R.bool.isLandscape);

        if (isLandscape && isTablet) {
            spanCount = 4;
        }

        if ((isTablet && !isLandscape) || (!isTablet && isLandscape)) {
            spanCount = 3;
        }

        return spanCount;
    }

    @SuppressLint("ShowToast")
    private void loadMovies(boolean isMostPopular) {
        boolean isOnline = NetworkUtils.isOnline(getApplicationContext());
        if (isOnline) {
            hideErrorMessage();
            setTitle(isMostPopular ? "Popular Movies" : "Rated Movies");
            recyclerView.setVisibility(page == 1 ? View.INVISIBLE : View.VISIBLE);
            new FetchMoviesTask(this).execute(isMostPopular);
        } else {
            if (snackBar == null) {
                snackBar = Snackbar.make(frameLayout, "Check your internet connection and Retry.", Snackbar.LENGTH_LONG);
            }
            snackBar.show();
        }
    }

    void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessageTV.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage() {
        errorMessageTV.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_MOST_POPULAR, isMostPopular);
        outState.putInt(PAGE_COUNT, page);
        outState.putParcelableArrayList(MOVIES, (ArrayList<Movie>) adapter.getMovies());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.sort_by_most_popular) {
            loadMostPopularMovies();
            return true;
        }

        if (id == R.id.sort_by_top_rated) {
            loadTopRatedMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadTopRatedMovies() {
        page = 1;
        isMostPopular = false;
        loadMovies(false);
    }

    private void loadMostPopularMovies() {
        page = 1;
        isMostPopular = true;
        loadMovies(true);
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(AppConstants.SELECTED_MOVIE, movie);
        startActivity(intent);
    }
}
