package com.example.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utilities.JSONUtils;
import com.example.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String IS_MOST_POPULAR = "is_most_popular";
    private static final String PAGE_COUNT = "page_count";
    private static Integer page = 1;
    private Boolean isMostPopular = true;
    private static Boolean isLoading = false;

    private ProgressBar progressBar;
    private TextView errorMessageTV;
    private RecyclerView recyclerView;
    private PopularMoviesAdapter adapter;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.pb_rv);
        errorMessageTV = findViewById(R.id.tv_error_message);
        recyclerView = findViewById(R.id.rv_movies);

        adapter = new PopularMoviesAdapter();

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), getSpanCount());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        if (savedInstanceState != null) {
            isMostPopular = savedInstanceState.getBoolean(IS_MOST_POPULAR);
            setTitle(isMostPopular ? "Popular Movies" : "Rated Movies");
            page = savedInstanceState.getInt(PAGE_COUNT);
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

        loadMovies(isMostPopular);
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
            if (toast == null) {
                toast = Toast.makeText(getApplicationContext(), "Check your internet connection and Retry.", Toast.LENGTH_LONG);
            }
            toast.show();
        }
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessageTV.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage() {
        errorMessageTV.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    static class FetchMoviesTask extends AsyncTask<Boolean, Void, List<Movie>> {
        final WeakReference<MainActivity> activityWeakReference;

        FetchMoviesTask (MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activityWeakReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(Boolean... options) {
            List<Movie> movies = new ArrayList<>();
            Context context = activityWeakReference.get().getApplicationContext();
            try {
                boolean isMostPopular = options[0];
                URL url = NetworkUtils.buildUrl(isMostPopular, page);
                String json = NetworkUtils.getHttpUrlResponse(url);
                movies = JSONUtils.getMovies(json, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            MainActivity mainActivity = activityWeakReference.get();
            mainActivity.progressBar.setVisibility(View.INVISIBLE);

            if (movies.isEmpty() && page == 1) {
                mainActivity.showErrorMessage();
            } else {
                mainActivity.hideErrorMessage();
                if (page == 1) {
                    mainActivity.adapter.setMovies(movies);
                } else {
                    isLoading = false;
                    mainActivity.adapter.addAll(movies);
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_MOST_POPULAR, isMostPopular);
        outState.putInt(PAGE_COUNT, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        page = 1;

        if (id == R.id.sort_by_most_popular) {
            isMostPopular = true;
            loadMovies(true);
            return true;
        }

        if (id == R.id.sort_by_top_rated) {
            isMostPopular = false;
            loadMovies(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
