package com.example.popularmovies.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

public class DetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Context context;
    private long movieId;

    public DetailsViewModelFactory(Context context, long movieId) {
        this.context = context;
        this.movieId = movieId;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailsViewModel(context, movieId);
    }
}
