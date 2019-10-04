package com.example.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.popularmovies.adapter.VideosAdapter;
import com.example.popularmovies.model.Video;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideosViewHolder extends RecyclerView.ViewHolder {
    private VideosAdapter videosAdapter;
    @BindView(R.id.ib_play)
    ImageButton playIB;
    @BindView(R.id.iv_video)
    ImageView videoIV;

    public VideosViewHolder(VideosAdapter videosAdapter, @NonNull View itemView) {
        super(itemView);
        this.videosAdapter = videosAdapter;
        ButterKnife.bind(this, itemView);
    }

    public void bind(Video video) {
        playIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideosViewHolder.this.onClick(v);
            }
        });

        Picasso.get()
                .load(video.getImageUrl())
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder)
                .fit()
                .into(videoIV);
    }

    @OnClick
    void onClick(View view) {
        videosAdapter.onVideoClick(videosAdapter.getVideos().get(getAdapterPosition()));
    }
}
