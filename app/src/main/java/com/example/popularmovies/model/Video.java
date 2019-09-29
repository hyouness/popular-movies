package com.example.popularmovies.model;

public class Video {
    private String id;
    private String name;
    private String key;
    private String videoUrl;
    private String imageUrl;

    public Video() {
    }

    public Video(String id, String name, String key, String videoUrl, String imageUrl) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
