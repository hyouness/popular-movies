package com.example.popularmovies.service;

import com.example.popularmovies.model.MovieList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiService {
    @GET("{sort_type}")
    Call<MovieList> getMovies(@Path("sort_type") String sortType, @Query("page") Integer page, @Query("api_key") String apiKey);
}
