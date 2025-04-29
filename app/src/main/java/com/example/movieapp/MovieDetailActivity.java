package com.example.movieapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String API_KEY = "79422230266c641ea333c4e89efbdd58";
    private TMDbApi api;

    private ImageView imageBackdrop, imagePoster;
    private TextView textTitle, textYear, textDuration, textRating, textGenres, textOverview, textDirectors, textWriters,
            textLanguages, textCountries, textCompanies, textCastLabel, textMoreLikeThisTitle;
    private boolean isFavorite = false;
    private Button buttonFavorite;
    FlexboxLayout genreContainer;

    private TextView buttonSeeAllCast;
    private List<CreditsResponse.Cast> castList; // Save full cast list


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        setContentView(R.layout.activity_movie_detail);

        int movieId = getIntent().getIntExtra("movie_id", -1);
        if (movieId == -1) {
            Toast.makeText(this, "Invalid movie ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        genreContainer = findViewById(R.id.genreContainer);
        textLanguages = findViewById(R.id.textLanguages);
        textCountries = findViewById(R.id.textCountries);
        textCompanies = findViewById(R.id.textCompanies);
        textCastLabel = findViewById(R.id.textCastLabel);
        textMoreLikeThisTitle = findViewById(R.id.textMoreLikeThisTitle);

        api = ApiClient.getClient().create(TMDbApi.class);

//        imageBackdrop = findViewById(R.id.imageBackdrop);
        imagePoster = findViewById(R.id.imagePoster);
        textTitle = findViewById(R.id.textTitle);
        textYear = findViewById(R.id.textYear);
        textDuration = findViewById(R.id.textDuration);
        textRating = findViewById(R.id.textRating);
//        textGenres = findViewById(R.id.textGenres);
        textOverview = findViewById(R.id.textOverview);
        textDirectors = findViewById(R.id.textDirectors);
        textWriters = findViewById(R.id.textWriters);
        buttonFavorite = findViewById(R.id.buttonFavorite);


        fetchMovieDetails(movieId);
        fetchMovieCredits(movieId);
        loadMoreLikeThis(movieId);
        fetchTrailer(movieId);

        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());


        buttonFavorite.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Please log in to favorite movies", Toast.LENGTH_SHORT).show();
                return;
            }

            if (movieId == -1) return;

            // You can change this to toggle instead of always adding
            if (isFavorite) {
                removeFavoriteMovie(user.getUid(), movieId);
            } else {
                storeFavoriteMovie(user.getUid(), movieId);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(userId)
                    .child("favorites")
                    .child(String.valueOf(movieId));

            ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    isFavorite = true;
                    updateFavoriteButtonUI(true);
                }
            });
        }
    }

    private void fetchTrailer(int movieId) {
        api.getMovieVideos(movieId, API_KEY).enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<VideoModel> videos = response.body().getResults();

                    if (videos != null && !videos.isEmpty()) {
                        for (VideoModel video : videos) {
                            if ("Trailer".equalsIgnoreCase(video.getType()) && "YouTube".equalsIgnoreCase(video.getSite())) {
                                String videoKey = video.getKey();
                                showTrailer(videoKey);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, "Failed to load trailer", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTrailer(String videoKey) {
        YouTubePlayerView youtubePlayerView = findViewById(R.id.youtubePlayerView);
        youtubePlayerView.setVisibility(View.VISIBLE);

        getLifecycle().addObserver(youtubePlayerView);

        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(videoKey, 0);
            }
        });
    }


    private void updateFavoriteButtonUI(boolean favorite) {
        isFavorite = favorite;

        if (favorite) {
            buttonFavorite.setText("In Favorites");
            buttonFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24, 0, 0, 0);
        } else {
            buttonFavorite.setText("Add to Favorites");
            buttonFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_add_24, 0, 0, 0);
        }
    }

    
    private void storeFavoriteMovie(String userId, int movieId) {
        api.getMovieDetails(movieId, API_KEY).enqueue(new Callback<MovieDetailModel>() {
            @Override
            public void onResponse(Call<MovieDetailModel> call, Response<MovieDetailModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetailModel movie = response.body();

                    FavoriteMovie favorite = new FavoriteMovie(
                            movie.getId(),
                            movie.getTitle(),
                            movie.getPosterUrl(),
                            movie.getReleaseYear(),
                            movie.getRating()
                    );

                    FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(userId)
                            .child("favorites")
                            .child(String.valueOf(movieId))
                            .setValue(favorite)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    isFavorite = true;
                                    updateFavoriteButtonUI(true);
//                                    Toast.makeText(MovieDetailActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onFailure(Call<MovieDetailModel> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, "Error adding to favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFavoriteMovie(String userId, int movieId) {
        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("favorites")
                .child(String.valueOf(movieId))
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        isFavorite = false;
                        updateFavoriteButtonUI(false);
//                        Toast.makeText(MovieDetailActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchMovieDetails(int movieId) {
        api.getMovieDetails(movieId, API_KEY).enqueue(new Callback<MovieDetailModel>() {
            @Override
            public void onResponse(Call<MovieDetailModel> call, Response<MovieDetailModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetailModel movie = response.body();
                    textTitle.setText(movie.getTitle());
                    textYear.setText(movie.getReleaseYear());
                    textDuration.setText(formatRuntime(movie.getRuntime()));
                    textRating.setText(String.format("⭐ %.1f", movie.getRating()));
                    textOverview.setText(movie.getOverview());

                    // Load poster image
                    Glide.with(MovieDetailActivity.this)
                            .load(movie.getPosterUrl())
                            .transform(new RoundedCorners(20))
                            .into(imagePoster);

                    // Set spoken languages
                    if (movie.getSpokenLanguages() != null && !movie.getSpokenLanguages().isEmpty()) {
                        StringBuilder langs = new StringBuilder();
                        for (MovieDetailModel.Language lang : movie.getSpokenLanguages()) {
                            if (langs.length() > 0) langs.append(", ");
                            langs.append(lang.getName());
                        }
                        textLanguages.setText(langs.toString());
                    }

                    // Set production countries
                    if (movie.getProductionCountries() != null && !movie.getProductionCountries().isEmpty()) {
                        StringBuilder countries = new StringBuilder();
                        for (MovieDetailModel.Country country : movie.getProductionCountries()) {
                            if (countries.length() > 0) countries.append(", ");
                            countries.append(country.getName());
                        }
                        textCountries.setText(countries.toString());
                    }

                    // Set production companies
                    if (movie.getProductionCompanies() != null && !movie.getProductionCompanies().isEmpty()) {
                        StringBuilder companies = new StringBuilder();
                        for (MovieDetailModel.Company company : movie.getProductionCompanies()) {
                            if (companies.length() > 0) companies.append(", ");
                            companies.append(company.getName());
                        }
                        textCompanies.setText(companies.toString());
                    }

                    // Set genres as bordered chips
                    if (movie.getGenresList() != null && !movie.getGenresList().isEmpty()) {
                        genreContainer.removeAllViews(); // genreContainer = FlexboxLayout in your layout

                        for (String genre : movie.getGenresList()) {
                            TextView chip = new TextView(MovieDetailActivity.this);
                            chip.setText(genre);
                            chip.setPadding(24, 12, 24, 12);
                            chip.setBackgroundResource(R.drawable.genre_border); // create this drawable
                            chip.setTextSize(14);

                            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(8, 8, 8, 8);
                            chip.setLayoutParams(params);

                            genreContainer.addView(chip);
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<MovieDetailModel> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String formatRuntime(int runtime) {
        int hours = runtime / 60;
        int minutes = runtime % 60;
        return hours + "h " + minutes + "m";
    }

    private void fetchMovieCredits(int movieId) {
        api.getMovieCredits(movieId, API_KEY).enqueue(new Callback<CreditsResponse>() {
            @Override
            public void onResponse(Call<CreditsResponse> call, Response<CreditsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CreditsResponse credits = response.body();

                    int castCount = credits.getCast() != null ? credits.getCast().size() : 0;
                    textCastLabel.setText("Cast (" + castCount + ")");

                    StringBuilder directors = new StringBuilder();
                    Map<String, List<String>> writerMap = new HashMap<>();

                    for (CreditsResponse.Crew crew : credits.getCrew()) {
                        if ("Director".equals(crew.getJob())) {
                            if (directors.length() > 0) directors.append(", ");
                            directors.append(crew.getName());
                        }

                        if ("Screenplay".equalsIgnoreCase(crew.getJob()) ||
                                "Writer".equalsIgnoreCase(crew.getJob()) ||
                                "Story".equalsIgnoreCase(crew.getJob())) {

                            String name = crew.getName();
                            String job = crew.getJob();

                            if (!writerMap.containsKey(name)) {
                                writerMap.put(name, new ArrayList<>());
                            }

                            if (!writerMap.get(name).contains(job)) { // avoid duplicate roles
                                writerMap.get(name).add(job);
                            }
                        }
                    }

                    StringBuilder writers = new StringBuilder();
                    for (Map.Entry<String, List<String>> entry : writerMap.entrySet()) {
                        if (writers.length() > 0) writers.append(", ");
                        String roles = String.join(", ", entry.getValue());
                        writers.append(entry.getKey()).append(" (").append(roles).append(")");
                    }

                    textDirectors.setText(directors.toString());
                    textWriters.setText(writers.toString());

                    RecyclerView recyclerCast = findViewById(R.id.recyclerCast);
                    recyclerCast.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    CastAdapter adapter = new CastAdapter(credits.getCast());
                    recyclerCast.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<CreditsResponse> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, "Failed to load credits", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMoreLikeThis(int movieId) {
        RecyclerView recycler = findViewById(R.id.recyclerMoreLikeThis);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        TMDbApi api = ApiClient.getClient().create(TMDbApi.class);
        api.getSimilarMovies(movieId, API_KEY).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MovieModel> similarMovies = response.body().getResults();

                    if (similarMovies == null || similarMovies.isEmpty()) {
                        textMoreLikeThisTitle.setVisibility(View.GONE);
                        recycler.setVisibility(View.GONE);
                    } else {
                        MovieAdapter adapter = new MovieAdapter(similarMovies);
                        recycler.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, "Failed to load similar movies", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
