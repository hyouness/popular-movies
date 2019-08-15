package com.example.popularmovies;

import android.os.AsyncTask;

import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utilities.JSONUtils;
import com.example.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class FetchMoviesTask extends AsyncTask<Boolean, Void, List<Movie>> {
    private final WeakReference<MainActivity> activityWeakReference;

    FetchMoviesTask(MainActivity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activityWeakReference.get().showProgressBar();
    }

    @Override
    protected List<Movie> doInBackground(Boolean... options) {
        List<Movie> movies = new ArrayList<>();
        MainActivity mainActivity = activityWeakReference.get();

        try {
            boolean isMostPopular = options[0];
            URL url = NetworkUtils.buildUrl(isMostPopular, mainActivity.getPageNumber());
            String json = NetworkUtils.getHttpUrlResponse(url);
            movies = JSONUtils.getMovies(json, mainActivity.getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movies;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);
        MainActivity mainActivity = activityWeakReference.get();
        mainActivity.updateRecyclerView(movies);
    }
}
