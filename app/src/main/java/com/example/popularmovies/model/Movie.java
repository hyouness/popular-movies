package com.example.popularmovies.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

@Entity(tableName = "movie")
public class Movie implements Parcelable {

    @PrimaryKey
    private long id;
    private String title;
    private String originalTitle;
    private String posterUrl;
    private String backdropImageUrl;
    private String overview;
    private String rating;
    private String releaseDate;
    private String voteCount;
    private Date bookmarkDate;

    public Movie() {

    }

    private Movie(Parcel in) {
        id = in.readLong();
        title = in.readString();
        originalTitle = in.readString();
        posterUrl = in.readString();
        backdropImageUrl = in.readString();
        overview = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
        voteCount = in.readString();
    }

    public static Builder newMovie(long id, String title, String posterUrl) {
        return new Builder(id, title, posterUrl);
    }

    public void setId(Long id) {
        this.id =  id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getBackdropImageUrl() {
        return backdropImageUrl;
    }

    public void setBackdropImageUrl(String backdropImageUrl) {
        this.backdropImageUrl = backdropImageUrl;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public Date getBookmarkDate() {
        return bookmarkDate;
    }

    public void setBookmarkDate(Date bookmarkDate) {
        this.bookmarkDate = bookmarkDate;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(originalTitle);
        parcel.writeString(posterUrl);
        parcel.writeString(backdropImageUrl);
        parcel.writeString(overview);
        parcel.writeString(rating);
        parcel.writeString(releaseDate);
        parcel.writeString(voteCount);
    }

    public static class Builder {
        private final long id;
        private final String title;
        private String originalTitle;
        private final String posterUrl;
        private String backdropImageUrl;
        private String overview;
        private String rating;
        private String releaseDate;
        private String voteCount;

        Builder (long id, String title, String posterUrl) {
            this.id = id;
            this.title = title;
            this.posterUrl = posterUrl;
        }

        public Builder originalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
            return this;
        }

        public Builder backdropImageUrl(String backdropImageUrl) {
            this.backdropImageUrl = backdropImageUrl;
            return this;
        }

        public Builder overview(String overview) {
            this.overview = overview;
            return this;
        }

        public Builder rating(String rating) {
            this.rating = rating;
            return this;
        }

        public Builder releaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public Builder voteCount(String voteCount) {
            this.voteCount = voteCount;
            return this;
        }

        public Movie build() {
            Movie movie = new Movie();
            movie.id = id;
            movie.title = title;
            movie.originalTitle = originalTitle;
            movie.posterUrl = posterUrl;
            movie.overview = overview;
            movie.backdropImageUrl = backdropImageUrl;
            movie.rating = rating;
            movie.releaseDate = releaseDate;
            movie.voteCount = voteCount;
            return movie;
        }
    }
}
