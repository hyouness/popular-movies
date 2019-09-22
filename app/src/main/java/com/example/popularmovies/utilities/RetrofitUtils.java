package com.example.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.popularmovies.AppConstants;
import com.example.popularmovies.model.MovieList;
import com.example.popularmovies.service.MovieApiService;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

final public class RetrofitUtils {

    private static Retrofit retrofit = null;
    private static MovieApiService apiService;

    private RetrofitUtils() {}

    private static Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS).build();

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(MovieList.class, new MovieListDeserializer(context));

            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                    .client(client)
                    .build();
        }

        return retrofit;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static MovieApiService getMovieService(Context context) {
        if (apiService == null) {
            apiService = getClient(context).create(MovieApiService.class);
        }
        return apiService;
    }
}
