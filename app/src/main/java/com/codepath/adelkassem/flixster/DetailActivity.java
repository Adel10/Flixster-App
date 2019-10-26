package com.codepath.adelkassem.flixster;

import androidx.databinding.DataBindingUtil;
import okhttp3.Headers;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.adelkassem.flixster.databinding.ActivityDetailBinding;
import com.codepath.adelkassem.flixster.databinding.ActivityMainBinding;
import com.codepath.adelkassem.flixster.models.Movie;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

public class DetailActivity extends YouTubeBaseActivity {

    public static final String TAG = "DetailActivity";
    private static final String YOUTUBE_API_KEY = BuildConfig.YOUTUBE_API_KEY;
    private static final String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    public static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=" + TMDB_API_KEY;
    public static final String MOVIE_DETAILS_URL = "https://api.themoviedb.org/3/movie/%d?api_key=" + TMDB_API_KEY;
    public static final String MOVIE_CREDITS_URL = "https://api.themoviedb.org/3/movie/%d/credits?api_key=" + TMDB_API_KEY;

    // Store the binding
    private ActivityDetailBinding binding;

    TextView tvTitle;
    TextView tvOverview;
    RatingBar ratingBar;
    YouTubePlayerView youTubePlayerView;
    TextView tvGenre;
    TextView tvDirector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the content view (replacing `setContentView`)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        setActivityBackgroundColor(Color.rgb(10, 10, 10));

        tvTitle = binding.tvTitle;
        tvOverview = binding.tvOverview;
        ratingBar = binding.ratingBar;
        youTubePlayerView = binding.player;
        tvGenre = binding.tvGenre;
        tvDirector = binding.tvDirector;

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvTitle.setText(movie.getTitle());
        tvTitle.setTextColor(Color.WHITE);
        tvOverview.setText(movie.getOverview());
        tvOverview.setTextColor(Color.LTGRAY);
        ratingBar.setRating((float) movie.getRating()/2);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEOS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");

                    // Setting place holder image if the results list is empty
                    if (results.length() == 0) {
                        youTubePlayerView.setBackgroundResource(R.drawable.place_holder_image);
                        return;
                    }
                    for (int i = 0; i < results.length(); i++) {
                        String site = results.getJSONObject(i).getString("site");
                        if (site.equals("YouTube")) {
                            String youtubeKey = results.getJSONObject(i).getString("key");
                            initializeYoutube(youtubeKey);
                            break;
                        }

                        // Setting place holder image if the results list does not have youtube keys
                        if (i == results.length() - 1 && !site.equals("YouTube")) {
                            youTubePlayerView.setBackgroundResource(R.drawable.place_holder_image);
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse JSON", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });

        client.get(String.format(MOVIE_DETAILS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray genres = jsonObject.getJSONArray("genres");

                    if (genres.length() != 0) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < genres.length(); i++) {
                            String genreName = genres.getJSONObject(i).getString("name");
                            if (i == genres.length() - 1) {
                                sb.append(genreName);
                                break;
                            }
                            sb.append(genreName + ", ");
                        }

                        tvGenre.setText("Genre: " + sb);
                        tvGenre.setTextColor(Color.rgb(100, 100, 100));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse JSON", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });

        client.get(String.format(MOVIE_CREDITS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray crew = jsonObject.getJSONArray("crew");

                    if (crew.length() != 0) {
                        String directorName = "";
                        for (int i = 0; i < crew.length(); i++) {
                            String jobTitle = crew.getJSONObject(i).getString("job");
                            if (jobTitle.equals("Director")) {
                                directorName = crew.getJSONObject(i).getString("name");
                                break;
                            }
                        }

                        if (!directorName.equals("")) {
                            tvDirector.setText("Director: " + directorName);
                            tvDirector.setTextColor(Color.rgb(100, 100, 100));
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse JSON", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });
    }

    private void initializeYoutube(final String youtubeKey) {
        youTubePlayerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.cueVideo(youtubeKey);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }
}
