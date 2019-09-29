package com.example.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.popularmovies.AppConstants;
import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.ResponseList;
import com.example.popularmovies.service.MovieApiService;
import com.example.popularmovies.utilities.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String FAVORITES = "favorites";

    private MutableLiveData<List<Movie>> movies = new MutableLiveData<>();
    private LiveData<List<Movie>> favoriteMovies;
    private String currentSearchType = POPULAR;
    private Integer currentPage = 1;
    private MovieApiService movieService;


    public MainViewModel(@NonNull Application application) {
        super(application);
        movieService = RetrofitUtils.getMovieService(application.getApplicationContext());
        AppDatabase appDatabase = AppDatabase.getInstance(application.getApplicationContext());
        favoriteMovies = appDatabase.movieDao().loadFavoriteMovies();
    }

    public MutableLiveData<List<Movie>> getMovies(Integer page, String searchType) {
        if (movies.getValue() == null || !currentSearchType.equals(searchType) || !currentPage.equals(page)) {
            currentPage = page;
            currentSearchType = searchType;
            loadMovies();
        }
        return movies;
    }

    private void loadMovies() {
        Call<ResponseList<Movie>> moviesCall = movieService.getMovies(currentSearchType, currentPage, AppConstants.API_KEY);
        moviesCall.enqueue(new Callback<ResponseList<Movie>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseList<Movie>> call, @NonNull Response<ResponseList<Movie>> response) {
                List<Movie> currentMovies = movies.getValue() != null ? movies.getValue() : new ArrayList<Movie>();
                ResponseList<Movie> body = response.body();
                if (body != null) {
                    if (currentPage == 1) {
                        currentMovies = body.getItems();
                    } else {
                        currentMovies.addAll(body.getItems());
                    }
                }
                movies.setValue(currentMovies);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseList<Movie>> call, @NonNull Throwable t) {
                movies.setValue(movies.getValue());
            }
        });
    }


    public MutableLiveData<List<Movie>> getMovies() {
        return movies;
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        currentSearchType = FAVORITES;
        return favoriteMovies;
    }

    public String getCurrentSearchType() {
        return currentSearchType;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }
}
