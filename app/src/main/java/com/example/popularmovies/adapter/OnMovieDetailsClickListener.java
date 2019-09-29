package com.example.popularmovies.adapter;

import com.example.popularmovies.model.Review;
import com.example.popularmovies.model.Video;

public interface OnMovieDetailsClickListener {
    void onVideoClick(Video video);
    void onReviewClick(Review review);
}
