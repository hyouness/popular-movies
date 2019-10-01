package com.example.popularmovies.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.popularmovies.AppConstants;
import com.example.popularmovies.model.ResponseList;
import com.example.popularmovies.model.Review;
import com.example.popularmovies.model.Video;
import com.example.popularmovies.service.MovieApiService;
import com.example.popularmovies.utilities.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsViewModel extends ViewModel {

    private final Long id;
    private MutableLiveData<ResponseList<Video>> videos = new MutableLiveData<>();
    private MutableLiveData<ResponseList<Review>> reviews = new MutableLiveData<>();

    private Integer currentPage = 1;

    private Integer lastPage = 1;
    private MovieApiService movieService;

    DetailsViewModel(Context context, Long id) {
        this.id = id;
        this.movieService = RetrofitUtils.getMovieService(context);
    }

    public MutableLiveData<ResponseList<Video>> getVideos() {
        if (videos.getValue() == null) {
            Call<ResponseList<Video>> videosCall = movieService.getVideos(id, AppConstants.API_KEY);
            videosCall.enqueue(new Callback<ResponseList<Video>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseList<Video>> call, @NonNull Response<ResponseList<Video>> response) {
                    videos.setValue(response.body());
                }

                @Override
                public void onFailure(@NonNull Call<ResponseList<Video>> call, @NonNull Throwable t) {
                    videos.setValue(videos.getValue());
                }
            });
        }
        return videos;
    }

    public MutableLiveData<ResponseList<Review>> getReviews(Integer page) {
        if (reviews.getValue() == null  || !currentPage.equals(page)) {
            currentPage = page;
            loadReviews();
        }
        return reviews;
    }

    private void loadReviews() {
        Call<ResponseList<Review>> reviewsCall = movieService.getReviews(id, currentPage, AppConstants.API_KEY);
        reviewsCall.enqueue(new Callback<ResponseList<Review>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseList<Review>> call, @NonNull Response<ResponseList<Review>> response) {
                ResponseList<Review> currentReviews = reviews.getValue() != null ? reviews.getValue() : new ResponseList<Review>();
                ResponseList<Review> body = response.body();
                if (body != null) {
                    if (currentPage == 1) {
                        currentReviews = body;
                        lastPage = body.getTotalPages();
                    } else {
                        currentReviews.getItems().addAll(body.getItems());
                    }
                }
                reviews.setValue(currentReviews);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseList<Review>> call, @NonNull Throwable t) {
                reviews.setValue(reviews.getValue());
            }
        });
    }

    public MutableLiveData<ResponseList<Review>> getReviews() {
        return reviews;
    }

    public boolean isNotOnLastPage() {
        return currentPage < lastPage;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }
}
