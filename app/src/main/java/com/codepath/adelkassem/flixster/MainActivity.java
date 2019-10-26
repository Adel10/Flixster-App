package com.codepath.adelkassem.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Headers;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codepath.adelkassem.flixster.adapters.MovieAdapter;
import com.codepath.adelkassem.flixster.adapters.NewMovieAdapter;
import com.codepath.adelkassem.flixster.adapters.PopularMovieAdapter;
import com.codepath.adelkassem.flixster.databinding.ActivityMainBinding;
import com.codepath.adelkassem.flixster.models.Movie;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + TMDB_API_KEY;
    public static final String POPULAR_MOVIES_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + TMDB_API_KEY;
    public static final String NEW_MOVIES_URL = "https://api.themoviedb.org/3/movie/upcoming?api_key=" + TMDB_API_KEY;
    private static final String TAG = "MainActivity";
    private List<Movie> moviesPopular;
    private List<Movie> moviesNowPlaying;
    private List<Movie> newReleaseMovies;

    // Store the binding
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the content view (replacing `setContentView`)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setActivityBackgroundColor(Color.rgb(25, 25, 25));

        RecyclerView rvMovies = binding.rvMovies;
        RecyclerView rvPopularList = binding.rvPopularList;
        RecyclerView rvNewReleaseList = binding.rvNewReleaseList;

        TextView nowPlayingCategory = binding.nowPlayingCategory;
        TextView popularCategory = binding.popularCategory;
        TextView newReleasesCategory = binding.newReleasesCategory;

        nowPlayingCategory.setTextColor(Color.WHITE);
        popularCategory.setTextColor(Color.WHITE);
        newReleasesCategory.setTextColor(Color.WHITE);

        moviesPopular = new ArrayList<>();
        final PopularMovieAdapter popularMoviesAdapter = new PopularMovieAdapter(this, moviesPopular);
        rvPopularList.setAdapter(popularMoviesAdapter);
        rvPopularList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        moviesNowPlaying = new ArrayList<>();
        final MovieAdapter movieAdapter = new MovieAdapter(this, moviesNowPlaying);
        rvMovies.setAdapter(movieAdapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        newReleaseMovies = new ArrayList<>();
        final NewMovieAdapter newMovieAdapter = new NewMovieAdapter(this, newReleaseMovies);
        rvNewReleaseList.setAdapter(newMovieAdapter);
        rvNewReleaseList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(NOW_PLAYING_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.d(TAG, "Results" + results.toString());

                    moviesNowPlaying.addAll(Movie.fromJsonArray(results));
                    movieAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Movies: " + moviesNowPlaying.size());
                } catch (JSONException e) {
                    Log.e(TAG, "json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });

        client.get(POPULAR_MOVIES_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.d(TAG, "Results" + results.toString());

                    moviesPopular.addAll(Movie.fromJsonArray(results));
                    popularMoviesAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Movies: " + moviesPopular.size());
                } catch (JSONException e) {
                    Log.e(TAG, "json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });

        client.get(NEW_MOVIES_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.d(TAG, "Results" + results.toString());

                    newReleaseMovies.addAll(Movie.fromJsonArray(results));
                    newMovieAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Movies: " + newReleaseMovies.size());
                } catch (JSONException e) {
                    Log.e(TAG, "json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });
    }

    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }

}
