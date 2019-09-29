package com.example.popularmovies.model;

public class Review {
    private String id;
    private String author;
    private CharSequence content;


    public Review(String id, String author, CharSequence content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public CharSequence getContent() {
        return content;
    }

    public void setContent(CharSequence content) {
        this.content = content;
    }

}
