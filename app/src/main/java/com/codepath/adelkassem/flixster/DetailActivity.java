package com.codepath.adelkassem.flixster;

import okhttp3.Headers;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

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
    private static final String YOUTUBE_API_KEY = "AIzaSyDBEL5_jtf9DAeHphQjbBQ8eI1OKCyRunY";
    public static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    TextView tvTitle;
    TextView tvOverview;
    RatingBar ratingBar;
    YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setActivityBackgroundColor(Color.rgb(10, 10, 10));

        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        ratingBar = findViewById(R.id.ratingBar);
        youTubePlayerView = findViewById(R.id.player);

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvTitle.setText(movie.getTitle());
        tvTitle.setTextColor(Color.rgb(255, 165, 0));
        tvOverview.setText(movie.getOverview());
        tvOverview.setTextColor(Color.WHITE);
        ratingBar.setRating((float) movie.getRating()/2);
        ratingBar.setBackgroundColor(Color.rgb(50, 50, 50));

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
