package com.example.popularmovies;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.popularmovies.utilities.RetrofitUtils;
import com.example.popularmovies.viewmodel.MainViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnMovieItemClickListener {

    private Boolean isLoading = false;

    @BindView(R.id.pb_rv)
    ProgressBar progressBar;
    @BindView(R.id.tv_error_message)
    TextView errorMessageTV;
    @BindView(R.id.rv_movies)
    RecyclerView recyclerView;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;

    private PopularMoviesAdapter adapter;
    private Snackbar snackBar;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new PopularMoviesAdapter(this);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), getSpanCount());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new PagingScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                if (!isLoading) {
                    isLoading = true;
                    loadMovies(viewModel.isCurrentSelection(), viewModel.getCurrentPage() + 1);
                }
            }

            @Override
            public boolean isLoading() { return isLoading; }
        });

        viewModel.getMovies(getApplicationContext(), viewModel.getCurrentPage(), viewModel.isCurrentSelection()).observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                hideProgressBar();
                if (movies != null && !movies.isEmpty()) {
                    updateRecyclerView(movies);
                } else if (!RetrofitUtils.isOnline(getApplicationContext())) {
                    checkInternetConnection();
                }
                isLoading = false;
            }
        });
    }

    void updateRecyclerView(List<Movie> movies) {
        if (movies.isEmpty() && viewModel.getCurrentPage() == 1) {
            showErrorMessage();
        } else {
            hideErrorMessage();
            if (viewModel.getCurrentPage() == 1) {
                adapter.setMovies(movies);
            } else {
                isLoading = false;
                adapter.setMovies(movies);
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
    private void loadMovies(boolean isMostPopular, Integer page) {
        isLoading = true;
        boolean isOnline = RetrofitUtils.isOnline(getApplicationContext());
        if (isOnline) {
            hideErrorMessage();
            setTitle(isMostPopular ? "Popular Movies" : "Rated Movies");
            recyclerView.setVisibility(page == 1 ? View.INVISIBLE : View.VISIBLE);
            queryMoviesService(isMostPopular, page);
        } else {
            isLoading = false;
            checkInternetConnection();
        }
    }

    private void checkInternetConnection() {
        if (snackBar == null) {
            snackBar = Snackbar.make(frameLayout, "Check your internet connection and Retry.", Snackbar.LENGTH_LONG);
        }
        snackBar.show();
    }

    private void queryMoviesService(boolean isMostPopular, Integer page) {
        showProgressBar();
        viewModel.getMovies(getApplicationContext(), page, isMostPopular);
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
        loadMovies(false, 1);
    }

    private void loadMostPopularMovies() {
        loadMovies(true, 1);
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(AppConstants.SELECTED_MOVIE, movie);
        startActivity(intent);
    }
}
