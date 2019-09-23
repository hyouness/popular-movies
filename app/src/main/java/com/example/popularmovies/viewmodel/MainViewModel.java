package com.example.popularmovies.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.popularmovies.AppConstants;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.MovieList;
import com.example.popularmovies.service.MovieApiService;
import com.example.popularmovies.utilities.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {
    private MutableLiveData<List<Movie>> movies = new MutableLiveData<>();
    private boolean currentSelection = true;
    private Integer currentPage = 1;

    public MainViewModel() {
    }

    public MutableLiveData<List<Movie>> getMovies(Context context, Integer page, boolean isMostPopular) {
        System.out.println(String.format("getMovies::: currentPage: %s - currentSelection: %s", currentPage, currentSelection));
        System.out.println(String.format("getMovies::: page: %s - isMostPopular: %s", page, isMostPopular));
        if (movies.getValue() == null || currentSelection != isMostPopular || !currentPage.equals(page)) {
            currentPage = page;
            currentSelection = isMostPopular;
            loadMovies(context);
        }
        return movies;
    }

    private void loadMovies(Context context) {
        MovieApiService movieService = RetrofitUtils.getMovieService(context);
        Call<MovieList> moviesCall = movieService.getMovies(currentSelection ? "popular" : "top_rated", currentPage, AppConstants.API_KEY);
        moviesCall.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(@NonNull Call<MovieList> call, @NonNull Response<MovieList> response) {
                List<Movie> currentMovies = movies.getValue() != null ? movies.getValue() : new ArrayList<Movie>();
                MovieList body = response.body();
                if (body != null) {
                    if (currentPage == 1) {
                        currentMovies = body.getMovies();
                    } else {
                        currentMovies.addAll(body.getMovies());
                    }
                }
                movies.setValue(currentMovies);
            }

            @Override
            public void onFailure(@NonNull Call<MovieList> call, @NonNull Throwable t) {

            }
        });
    }

    public boolean isCurrentSelection() {
        return currentSelection;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }
}
