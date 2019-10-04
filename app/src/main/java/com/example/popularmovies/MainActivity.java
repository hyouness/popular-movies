package com.example.popularmovies;

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

import com.example.popularmovies.adapter.MoviesAdapter;
import com.example.popularmovies.adapter.OnMovieClickListener;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utilities.RetrofitUtils;
import com.example.popularmovies.viewmodel.MainViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnMovieClickListener {

    private Boolean isLoading = false;

    @BindView(R.id.pb_rv)
    ProgressBar progressBar;
    @BindView(R.id.tv_error_message)
    TextView errorMessageTV;
    @BindView(R.id.rv_movies)
    RecyclerView recyclerView;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;

    private MoviesAdapter adapter;
    private Snackbar snackBar;
    private MainViewModel viewModel;
    private Observer<List<Movie>> favoriteMoviesObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new MoviesAdapter(this);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), getSpanCount());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new PagingScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                if (!isLoading && !viewModel.getCurrentSearchType().equals(MainViewModel.FAVORITES)) {
                    loadNewMovies(viewModel.getCurrentSearchType(), viewModel.getCurrentPage() + 1);
                }
            }

            @Override
            public boolean isLoading() { return isLoading; }
        });

        if (viewModel.getCurrentSearchType().equals(MainViewModel.FAVORITES)) {
            queryMoviesDatabase();
        } else {
            if (RetrofitUtils.isOnline(getApplicationContext()) || viewModel.getMovies().getValue() != null) {
                queryMoviesService(viewModel.getCurrentSearchType(), viewModel.getCurrentPage());
            } else if (viewModel.getMovies().getValue() == null || viewModel.getMovies().getValue().isEmpty()){
                showErrorMessage(R.string.error_message);
            }
        }
    }

    private void queryMoviesDatabase() {
        hideErrorMessage();
        showProgressBar();
        if (!viewModel.getFavoriteMovies().hasObservers()) {
            favoriteMoviesObserver = new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    if (viewModel.getCurrentSearchType().equals(MainViewModel.FAVORITES)) {
                        hideProgressBar();
                        setTitle(getTitle(viewModel.getCurrentSearchType()));
                        if (movies != null && !movies.isEmpty()) {
                            updateRecyclerView(movies);
                        } else {
                            showErrorMessage(R.string.no_favorites);
                        }
                    }
                }
            };
            viewModel.getFavoriteMovies().observe(this, favoriteMoviesObserver);
        } else {
            favoriteMoviesObserver.onChanged(viewModel.getFavoriteMovies().getValue());
        }
    }

    private void queryMoviesService(String searchType, final Integer page) {
        hideErrorMessage();
        showProgressBar();
        if (!viewModel.getMovies().hasObservers()) {
            viewModel.getMovies(searchType, page).observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    hideProgressBar();
                    if (movies != null && !movies.isEmpty()) {
                        updateRecyclerView(movies);
                    } else if (viewModel.getCurrentPage() == 1) {
                        showErrorMessage(R.string.error_message);
                    } else if (!RetrofitUtils.isOnline(getApplicationContext())) {
                        checkInternetConnection();
                    }
                    isLoading = false;
                }
            });
        } else {
            viewModel.getMovies(searchType, page);
        }
    }

    void updateRecyclerView(List<Movie> movies) {
        recyclerView.setVisibility(View.VISIBLE);
        setTitle(getTitle(viewModel.getCurrentSearchType()));
        if (viewModel.getCurrentPage() == 1 || viewModel.getCurrentSearchType().equals(MainViewModel.FAVORITES)) {
            recyclerView.scrollToPosition(0);
        }
        adapter.setMovies(movies);
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

    private static String getTitle(String searchType) {
        if (searchType.equals(MainViewModel.POPULAR)) return "Popular Movies";
        else if (searchType.equals(MainViewModel.TOP_RATED)) return "Rated Movies";
        else return "Favorite Movies";
    }

    private void checkInternetConnection() {
        if (snackBar == null) {
            snackBar = Snackbar.make(frameLayout, R.string.check_internet, Snackbar.LENGTH_LONG);
        }
        snackBar.show();
    }

    void showProgressBar() {
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage(int message) {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessageTV.setText(message);
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
            loadNewMovies(MainViewModel.POPULAR, 1);
            return true;
        }

        if (id == R.id.sort_by_top_rated) {
            loadNewMovies(MainViewModel.TOP_RATED, 1);
            return true;
        }

        if (id == R.id.sort_by_favorites) {
            queryMoviesDatabase();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadNewMovies(String searchType, Integer page) {
        if (!RetrofitUtils.isOnline(getApplicationContext())) {
            checkInternetConnection();
        } else {
            queryMoviesService(searchType, page);
        }
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(AppConstants.SELECTED_MOVIE, movie);
        startActivity(intent);
    }
}
