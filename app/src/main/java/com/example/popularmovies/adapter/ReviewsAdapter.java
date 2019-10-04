package com.example.popularmovies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.popularmovies.R;
import com.example.popularmovies.ReviewsViewHolder;
import com.example.popularmovies.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsViewHolder> {

    private List<Review> reviews = new ArrayList<>();

    private final OnMovieDetailsClickListener onReviewItemClickListener;

    public ReviewsAdapter(OnMovieDetailsClickListener onReviewItemClickListener) {
        this.onReviewItemClickListener = onReviewItemClickListener;
    }

    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.reviews_list_item, viewGroup, false);
        return new ReviewsViewHolder(this, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder viewHolder, int i) {
        Review review = reviews.get(i);
        viewHolder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void onReviewClick(Review review) {
        onReviewItemClickListener.onReviewClick(review);
    }
}
