package com.example.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.adapter.MoviesAdapter;
import com.example.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoviesViewHolder extends RecyclerView.ViewHolder {
    private MoviesAdapter moviesAdapter;
    @BindView(R.id.iv_movie)
    ImageView movieIV;
    @BindView(R.id.tv_movie_title)
    TextView movieTitleTV;

    public MoviesViewHolder(MoviesAdapter moviesAdapter, @NonNull View itemView) {
        super(itemView);
        this.moviesAdapter = moviesAdapter;
        ButterKnife.bind(this, itemView);
    }

    public void bind(Movie movie) {
        movieTitleTV.setText(movie.getTitle());
        Picasso.get()
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder)
                .fit()
                .into(movieIV);
    }

    @OnClick
    void onClick(View view) {
        moviesAdapter.onMovieClick(moviesAdapter.getMovies().get(getAdapterPosition()));
    }
}
