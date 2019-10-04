package com.example.popularmovies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.popularmovies.R;
import com.example.popularmovies.VideosViewHolder;
import com.example.popularmovies.model.Video;

import java.util.ArrayList;
import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosViewHolder> {

    private List<Video> videos = new ArrayList<>();

    private final OnMovieDetailsClickListener onVideoItemClickListener;

    public VideosAdapter(OnMovieDetailsClickListener onVideoItemClickListener) {
        this.onVideoItemClickListener = onVideoItemClickListener;
    }

    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.videos_list_item, viewGroup, false);
        return new VideosViewHolder(this, view);
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

    public List<Video> getVideos() {
        return videos;
    }

    public void onVideoClick(Video video) {
        onVideoItemClickListener.onVideoClick(video);
    }
}
