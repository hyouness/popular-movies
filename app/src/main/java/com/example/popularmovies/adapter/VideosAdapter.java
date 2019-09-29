package com.example.popularmovies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.popularmovies.R;
import com.example.popularmovies.model.Video;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosViewHolder> {

    private List<Video> videos = new ArrayList<>();

    private final OnMovieDetailsClickListener onVideoItemClickListener;

    public VideosAdapter(OnMovieDetailsClickListener onVideoItemClickListener) {
        this.onVideoItemClickListener = onVideoItemClickListener;
    }

    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.movies_list_item, viewGroup, false);
        return new VideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosViewHolder viewHolder, int i) {
        Video video = videos.get(i);
        viewHolder.bind(video);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }

    class VideosViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.iv_movie)
//        ImageView movieIV;
//        @BindView(R.id.tv_movie_title)
//        TextView movieTitleTV;

        VideosViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Video video) {
//            movieTitleTV.setText(movie.getTitle());
//            Picasso.get()
//                    .load(movie.getPosterUrl())
//                    .placeholder(R.drawable.movie_placeholder)
//                    .error(R.drawable.movie_placeholder)
//                    .fit()
//                    .into(movieIV);
        }

        @OnClick
        void onClick(View view) {
            onVideoItemClickListener.onVideoClick(videos.get(getAdapterPosition()));
        }
    }
}
