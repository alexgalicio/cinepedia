package com.example.movieapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity {

    private TMDbApi api;
    private static final String API_KEY = "79422230266c641ea333c4e89efbdd58";
    private Handler sliderHandler = new Handler();
    private int currentPage = 0;
    private Runnable sliderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        api = ApiClient.getClient().create(TMDbApi.class);

        setupCarousel();

        setupSection(R.id.sectionPopular, "Popular Movies", "popular", api.getPopularMovies(API_KEY, "PH"));
        setupSection(R.id.sectionTopRated, "Top Rated Movies", "top_rated", api.getTopRatedMovies(API_KEY));
        setupSection(R.id.sectionNowPlaying, "In Theatres (PH)", "now_playing", api.getNowPlaying(API_KEY, "PH"));
        setupSection(R.id.sectionUpcoming, "Upcoming Movies", "upcoming", api.getUpcomingMovies(API_KEY));

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_account) {
                 startActivity(new Intent(this, AccountActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void setupSection(int sectionId, String title, String categoryKey, Call<MovieResponse> call) {
        View section = findViewById(sectionId);
        TextView textTitle = section.findViewById(R.id.textSectionTitle);
        TextView textSeeAll = section.findViewById(R.id.textViewAll);
        RecyclerView recycler = section.findViewById(R.id.recyclerMovies);

        textTitle.setText(title);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        textSeeAll.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, MovieListActivity.class);
            intent.putExtra("category", categoryKey);
            intent.putExtra("title", title);
            startActivity(intent);
        });

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MovieModel> movies = response.body().getResults();
                    MovieAdapter adapter = new MovieAdapter(movies);
                    recycler.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(Dashboard.this, "Failed to load movies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCarousel() {
        ViewPager2 carousel = findViewById(R.id.viewPagerCarousel);

        api.getTrendingThisWeek(API_KEY).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MovieModel> movies = response.body().getResults();
                    CarouselAdapter adapter = new CarouselAdapter(movies); // pass API_KEY if needed
                    carousel.setAdapter(adapter);

                    sliderRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (adapter.getItemCount() > 0) {
                                currentPage = (currentPage + 1) % adapter.getItemCount();
                                carousel.setCurrentItem(currentPage, true);
                                sliderHandler.postDelayed(this, 4000); // every 4 seconds
                            }
                        }
                    };
                    sliderHandler.postDelayed(sliderRunnable, 4000);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(Dashboard.this, "Carousel failed to load", Toast.LENGTH_SHORT).show();
            }
        });

        carousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPage = position;
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 4000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}
