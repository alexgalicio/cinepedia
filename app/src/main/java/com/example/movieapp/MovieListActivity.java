package com.example.movieapp;

import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListActivity extends AppCompatActivity {

    private static final String API_KEY = "79422230266c641ea333c4e89efbdd58";
    private TMDbApi api;
    private RecyclerView recyclerView;
    private TextView textViewTitle;
    private MovieAdapter adapter;
    private List<MovieModel> movies;

    private boolean isLoading = false;
    private int currentPage = 1;
    private int totalPages = 1;
    private String category;

    private int genreId;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_list);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        textViewTitle = findViewById(R.id.textViewTitle);
        recyclerView = findViewById(R.id.recyclerViewMovies);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        category = getIntent().getStringExtra("category");
        genreId = getIntent().getIntExtra("genre_id", -1);
        searchQuery = getIntent().getStringExtra("search_query");

        String title = getIntent().getStringExtra("title");
        if (title != null) {
            textViewTitle.setText(title);
        }

        api = ApiClient.getClient().create(TMDbApi.class);
        movies = new ArrayList<>();
        adapter = new MovieAdapter(movies);
        recyclerView.setAdapter(adapter);

        loadMovies(currentPage);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager) rv.getLayoutManager();
                if (!isLoading && layoutManager != null && layoutManager.findLastVisibleItemPosition() >= movies.size() - 5) {
                    if (currentPage < totalPages) {
                        loadMovies(++currentPage);
                    }
                }
            }
        });

        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());
    }

    private void loadMovies(int page) {
        isLoading = true;

        Call<MovieResponse> call;

        if (genreId != -1) {
            call = api.getMoviesByGenre(API_KEY, genreId, page);
        } else if (searchQuery != null && !searchQuery.isEmpty()) {
            call = api.searchMovies(API_KEY, searchQuery, page);
        } else {
            call = getCallForCategory(category, page);
        }

        if (call == null) {
            isLoading = false;
            Toast.makeText(this, "Invalid request", Toast.LENGTH_SHORT).show();
            return;
        }

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    movies.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
                    totalPages = response.body().getTotalPages();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                isLoading = false;
                Toast.makeText(MovieListActivity.this, "Failed to load movies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Call<MovieResponse> getCallForCategory(String category, int page) {
        if (category == null) return null;

        switch (category) {
            case "popular":
                return api.getPopularMovies(API_KEY, "PH", page);
            case "top_rated":
                return api.getTopRatedMovies(API_KEY, page);
            case "now_playing":
                return api.getNowPlaying(API_KEY, "PH", page);
            case "upcoming":
                return api.getUpcomingMovies(API_KEY, page);
            default:
                return null;
        }
    }
}
