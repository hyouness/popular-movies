package com.example.popularmovies.service;

import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.ResponseList;
import com.example.popularmovies.model.Review;
import com.example.popularmovies.model.Video;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiService {
    @GET("{sort_type}")
    Call<ResponseList<Movie>> getMovies(@Path("sort_type") String sortType, @Query("page") Integer page, @Query("api_key") String apiKey);

    @GET("{id}/videos")
    Call<ResponseList<Video>> getVideos(@Path("id") Long id, @Query("api_key") String apiKey);

    @GET("{id}/reviews")
    Call<ResponseList<Review>> getReviews(@Path("id") Long id, @Query("page") Integer page, @Query("api_key") String apiKey);

}
